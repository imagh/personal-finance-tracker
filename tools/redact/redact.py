#!/usr/bin/env python3
"""Redact bank SMS / email exports locally so samples can be shared safely.

Stdlib only, no network. Guide: docs/guides/collecting-sample-messages.md

    python3 tools/redact/redact.py --input sms.xml --out ./redacted-out

Output (in --out): redacted.jsonl and REVIEW.md. REVIEW EVERY LINE before sharing:
the heuristics are good, not perfect, and names/merchants cannot be told apart reliably.
"""
import argparse, hashlib, hmac, html, json, mailbox, re, secrets, sys
import xml.etree.ElementTree as ET
from datetime import datetime, timezone
from email import message_from_bytes, policy
from email.utils import parseaddr, parsedate_to_datetime
from html.parser import HTMLParser
from pathlib import Path

MARK = ("", "")  # private-use delimiters for held (already-redacted) spans
OTP_RE = re.compile(r"\botp\b|one[- ]time (?:password|pin)|verification code|\bcvv\b|\bpin\b|passcode", re.I)
TXN_RE = re.compile(r"debit|credit|spent|withdraw|paid|payment|received|\bemi\b|statement|due|balance|refund|revers|mandate|loan|limit|\bupi\b|neft|imps|rtgs|transfer|txn|transaction", re.I)
SENDER_RE = re.compile(r"^[A-Z]{2}-([A-Z0-9]{3,8})(?:-[A-Z])?$")
URL_RE = re.compile(r"https?://\S+|\bwww\.\S+", re.I)
EMAIL_RE = re.compile(r"[\w.+-]+@[\w-]+(?:\.[\w-]+)+")
VPA_RE = re.compile(r"\b[\w.\-]{2,}@[A-Za-z]{2,}\b")
PAN_RE = re.compile(r"\b[A-Z]{5}\d{4}[A-Z]\b")
PHONE_RE = re.compile(r"(?<!\d)(?:\+91[\s-]?|0)?[6-9]\d{9}(?!\d)")
AMOUNT_RE = re.compile(r"(?:\brs\.?|\binr|₹)\s*-?\s*\d[\d,]*(?:\.\d+)?", re.I)
BALANCE_CTX = re.compile(r"bal|available|avl|limit|outstanding|\bdue\b", re.I)
FRAG1_RE = re.compile(r"(?<![\w])([xX*]{1,12}-?)(\d{3,6})(?!\d)")
FRAG2_RE = re.compile(
    r"\b(ending(?:\s+(?:with|in))?|a/c(?:\s*no\.?)?|acct?(?:\s*no\.?)?|account(?:\s*no\.?)?|card(?:\s*no\.?)?|"
    r"loan(?:\s*a/c)?(?:\s*no\.?)?|ref(?:erence)?(?:\s*no\.?)?|txn(?:\s*(?:id|no\.?))?|utr|rrn|auth(?:\s*code)?|"
    r"mandate(?:\s*id)?)(\s*[:.#-]?\s*)(\d{4,8})(?!\d)", re.I)
LONG_RE = re.compile(r"\d{9,}")
GREET_RE = re.compile(r"\b(Dear|Hi|Hello)([\s,]+)((?:Mr|Ms|Mrs|Shri|Smt)\.?\s+)?([A-Za-z][\w.\-]*(?:\s+[A-Za-z][\w.\-]*){0,3})(?=[,:\n.!]|\s*$)")
CUE_RE = re.compile(r"\b((?:to|from|by)\s+)(?!VPA\b)([A-Z][A-Za-z.]+(?:\s+[A-Z][A-Za-z.]+){0,3})")
GENERIC_GREETEES = {"customer", "user", "sir", "madam", "cardmember", "member", "valued", "client", "team"}
STOP_WORDS = {"on", "at", "via", "ref", "upi", "info", "avl", "bal", "if", "not", "call", "thank", "thanks", "your", "the", "a", "an", "for", "is", "has", "was", "and"}
DEFAULT_KEEP = {w for w in """hdfc sbi icici axis kotak pnb canara baroda boi idfc indusind federal yes bank ltd limited card cards credit
swiggy zomato amazon flipkart uber ola netflix spotify google jio airtel vodafone bigbasket blinkit zepto irctc phonepe paytm gpay
bhim cred myntra ajio lic bsnl tata reliance dmart apollo hotstar youtube apple microsoft razorpay billdesk""".split()}


class Pseudo:
    """Keyed, per-run pseudonyms: same input -> same output within a run; never persisted."""

    def __init__(self):
        self.key = secrets.token_bytes(32)
        self.names = {}

    def stream(self, kind, value, n):
        out, i = "", 0
        while len(out) < n:
            d = hmac.new(self.key, f"{kind}:{i}:{value}".encode(), hashlib.sha256).digest()
            out += "".join(str(b % 10) for b in d)
            i += 1
        return out[:n]

    def swap(self, kind, s):
        """Replace each digit in s with a pseudonymous digit, keeping separators and length."""
        digs = [c for c in s if c.isdigit()]
        new = list(self.stream(kind, "".join(digs), len(digs)))
        if digs and digs[0] != "0" and new[0] == "0":
            new[0] = "1"
        it = iter(new)
        return "".join(next(it) if c.isdigit() else c for c in s)

    def person(self, key):
        return self.names.setdefault(key.lower(), f"PERSON{len(self.names) + 1}")


SLASH_RE = re.compile(r"(\d{9,}/)([A-Za-z][A-Za-z .]{1,40}?)(?=/)")


def mask_run(run, p, keep):
    """Mask a run of words as a person unless it contains a known org/merchant word."""
    words = run.split()
    if any(w.strip(".").lower() in keep for w in words):
        return run
    for i, w in enumerate(words):
        if w.lower() in STOP_WORDS:
            return (p.person(" ".join(words[:i])) + " " + " ".join(words[i:])) if i else run
    return p.person(run)


def redact_text(text, p, amounts="balances", keep=frozenset(), flags=None):
    held = []

    def hold(s):
        held.append(s)
        return f"{MARK[0]}{len(held) - 1}{MARK[1]}"

    keep = DEFAULT_KEEP | {k.lower() for k in keep}
    text = URL_RE.sub(lambda m: hold("https://redacted.example/x"), text)
    text = EMAIL_RE.sub(lambda m: hold("redacted@example.invalid"), text)
    text = VPA_RE.sub(lambda m: hold("redacted@upi"), text)
    text = PAN_RE.sub(lambda m: hold("ABCDE1234F"), text)
    text = PHONE_RE.sub(lambda m: hold("9" + p.stream("phone", m.group(0), 9)), text)

    def amt(m):
        ctx = m.string[max(0, m.start() - 30):m.start()]
        if amounts == "all" or (amounts == "balances" and BALANCE_CTX.search(ctx)):
            return hold(re.sub(r"[\d,.]+", lambda d: p.swap("amt", d.group(0)), m.group(0)))
        return hold(m.group(0))
    text = AMOUNT_RE.sub(amt, text)

    text = SLASH_RE.sub(lambda m: m.group(1) + mask_run(m.group(2), p, keep), text)
    text = FRAG1_RE.sub(lambda m: hold(m.group(1) + p.swap("frag", m.group(2))), text)
    text = FRAG2_RE.sub(lambda m: hold(m.group(1) + m.group(2) + p.swap("ref", m.group(3))), text)
    text = LONG_RE.sub(lambda m: hold(p.swap("long", m.group(0))), text)

    def greet(m):
        if m.group(4).lower().split()[0] in GENERIC_GREETEES:
            return m.group(0)
        return f"{m.group(1)}{m.group(2)}{m.group(3) or ''}{p.person(m.group(4))}"
    text = GREET_RE.sub(greet, text)

    def cue(m):
        words, masked = m.group(2).split(), []
        if any(w.strip(".").lower() in keep for w in words):
            return m.group(0)
        for i, w in enumerate(words):
            if w.lower() in STOP_WORDS:
                masked = words[:i]
                tail = " " + " ".join(words[i:])
                break
        else:
            masked, tail = words, ""
        if not masked:
            return m.group(0)
        return m.group(1) + p.person(" ".join(masked)) + tail
    text = CUE_RE.sub(cue, text)

    if flags is not None:  # digits still visible here were NOT handled by any rule
        scrub = re.sub(r"\d{1,2}[-/.]\w{2,3}[-/.]\d{2,4}|\d{1,2}:\d{2}(?::\d{2})?|\d[\d,]*\.\d+|" + MARK[0] + r"\d+" + MARK[1], "", text)
        if re.search(r"\d{5,}", scrub):
            flags.append("unhandled long digit run")
    return re.sub(f"{MARK[0]}(\\d+){MARK[1]}", lambda m: held[int(m.group(1))], text)


def review_flags(text, keep=frozenset()):
    flags = []
    if re.search(r"[\w.-]+@(?!example\.invalid|upi\b)\w", text):
        flags.append("possible email/VPA")
    for m in CUE_RE.finditer(text):
        if not re.match(r"PERSON\d+", m.group(2)) and not any(w.strip(".").lower() in DEFAULT_KEEP | {k.lower() for k in keep} for w in m.group(2).split()):
            flags.append(f"possible name after '{m.group(1).strip()}': {m.group(2)}")
    for m in re.finditer(r"/([A-Z][A-Z .]{2,40}?)/", text):
        if " " in m.group(1).strip() and not any(w.lower() in DEFAULT_KEEP | {k.lower() for k in keep} for w in m.group(1).split()):
            flags.append(f"possible name in narration: {m.group(1)}")
    if re.search(r"\b(road|street|nagar|colony|sector|flat|apartment|pincode)\b", text, re.I):
        flags.append("possible address")
    return flags


class _Text(HTMLParser):
    def __init__(self):
        super().__init__()
        self.parts, self.skip = [], 0

    def handle_starttag(self, tag, attrs):
        self.skip += tag in ("script", "style")
        if tag in ("br", "p", "div", "tr", "li"):
            self.parts.append("\n")

    def handle_endtag(self, tag):
        self.skip -= tag in ("script", "style") and self.skip > 0

    def handle_data(self, data):
        if not self.skip:
            self.parts.append(data)


def html_to_text(s):
    t = _Text()
    t.feed(s)
    return re.sub(r"[ \t\xa0]+", " ", re.sub(r"\n\s*\n+", "\n", "".join(t.parts))).strip()


def _day(ms):
    return datetime.fromtimestamp(int(ms) / 1000, tz=timezone.utc).strftime("%Y-%m-%d")


def read_sms_xml(path):
    for e in ET.parse(path).getroot().iter("sms"):
        yield dict(channel="sms", sender=e.get("address", ""), date=_day(e.get("date", "0")), text=e.get("body", ""))


ADB_ROW = re.compile(r"^Row: \d+ address=(.*?), date=(\d+), body=(.*)$", re.S)


def read_adb(path):
    for chunk in re.split(r"\n(?=Row: \d+ )", Path(path).read_text(errors="replace").strip()):
        m = ADB_ROW.match(chunk.strip())
        if m:
            yield dict(channel="sms", sender=m.group(1), date=_day(m.group(2)), text=m.group(3))


def read_jsonl(path):
    for line in Path(path).read_text().splitlines():
        if line.strip():
            d = json.loads(line)
            yield dict(channel="sms", sender=d.get("address", ""), date=_day(d.get("date", 0)), text=d.get("body", ""))


def _email_record(msg):
    body = msg.get_body(preferencelist=("plain", "html"))
    text = body.get_content() if body else ""
    if body and body.get_content_type() == "text/html":
        text = html_to_text(text)
    try:
        date = parsedate_to_datetime(msg["Date"]).strftime("%Y-%m-%d")
    except Exception:
        date = ""
    dom = parseaddr(msg["From"] or "")[1].split("@")[-1].lower()
    return dict(channel="email", sender="*@" + dom, date=date, subject=msg["Subject"] or "", text=text[:6000])


def read_eml(path):
    yield _email_record(message_from_bytes(Path(path).read_bytes(), policy=policy.default))


def read_mbox(path):
    for m in mailbox.mbox(path):
        yield _email_record(message_from_bytes(m.as_bytes(), policy=policy.default))


def read_any(path):
    path = Path(path)
    if path.is_dir():
        for f in sorted(path.rglob("*")):
            if f.is_file():
                yield from read_any(f)
        return
    suffix = path.suffix.lower()
    if suffix == ".xml":
        yield from read_sms_xml(path)
    elif suffix == ".eml":
        yield from read_eml(path)
    elif suffix == ".mbox":
        yield from read_mbox(path)
    elif suffix in (".jsonl", ".json"):
        yield from read_jsonl(path)
    elif suffix in (".txt", ".log"):
        yield from read_adb(path)


def process(records, amounts="balances", keep=frozenset(), drop_nonfinancial=True):
    p, out, stats = Pseudo(), [], dict(read=0, dropped_otp=0, dropped_personal_sender=0, dropped_nonfinancial=0)
    for r in records:
        stats["read"] += 1
        text = r["text"]
        if OTP_RE.search(text + " " + r.get("subject", "")):
            stats["dropped_otp"] += 1
            continue
        sender = r["sender"]
        if r["channel"] == "sms":
            m = SENDER_RE.match(sender)
            if not m and not re.fullmatch(r"[A-Za-z][A-Za-z0-9&-]{2,10}", sender):
                stats["dropped_personal_sender"] += 1
                continue
            sender = m.group(1) if m else sender
        if drop_nonfinancial and not TXN_RE.search(text + " " + r.get("subject", "")):
            stats["dropped_nonfinancial"] += 1
            continue
        flags = []
        rec = dict(id=f"m{len(out) + 1:04d}", channel=r["channel"], sender=sender, date=r["date"],
                   text=redact_text(text, p, amounts, keep, flags))
        if r.get("subject"):
            rec["subject"] = redact_text(r["subject"], p, amounts, keep, flags)
        rec["review_flags"] = flags + review_flags(rec["text"], keep)
        out.append(rec)
    stats["kept"] = len(out)
    return out, stats


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--input", required=True, nargs="+", help="SMS Backup&Restore .xml, adb dump .txt, .jsonl, .eml, .mbox, or a folder")
    ap.add_argument("--out", default="redacted-out", help="output folder (keep it out of git)")
    ap.add_argument("--amounts", choices=["keep", "balances", "all"], default="balances",
                    help="keep: leave all amounts; balances: randomise balances/limits/dues (default); all: randomise every amount")
    ap.add_argument("--keep-words", default="", help="file of extra merchant/org words (one per line) NOT to mask as names")
    ap.add_argument("--keep-nonfinancial", action="store_true", help="do not drop messages that lack transaction keywords")
    a = ap.parse_args(argv)
    keep = set(Path(a.keep_words).read_text().split()) if a.keep_words else set()
    recs = (r for i in a.input for r in read_any(i))
    out, stats = process(recs, a.amounts, keep, not a.keep_nonfinancial)
    od = Path(a.out)
    od.mkdir(parents=True, exist_ok=True)
    (od / "redacted.jsonl").write_text("".join(json.dumps(r, ensure_ascii=False) + "\n" for r in out))
    flagged = [r for r in out if r["review_flags"]]
    lines = ["# Redaction review", "", f"Stats: {stats}", "", f"{len(flagged)} of {len(out)} messages carry flags. "
             "Still read EVERY line of redacted.jsonl before sharing; no flags does not mean safe.", ""]
    for r in flagged:
        lines += [f"## {r['id']} ({r['sender']})", "- " + "\n- ".join(r["review_flags"]), "```", r["text"], "```", ""]
    (od / "REVIEW.md").write_text("\n".join(lines))
    print(f"kept {stats['kept']}/{stats['read']}; flagged {len(flagged)}; wrote {od}/redacted.jsonl and REVIEW.md")
    print("Do NOT commit or share anything until you have read redacted.jsonl.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
