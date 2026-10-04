# Collecting and redacting sample SMS / email

Goal: give the project **real message formats** without sharing personal data. Everything below runs on your own machine. Nothing is uploaded.

> **Rules**
> 1. Raw exports (`.xml`, `.mbox`, `.eml`) never go into this repo or a chat. They stay on your computer. `.gitignore` already blocks them.
> 2. Only the **redacted** output, after you have read it line by line, may be shared or committed (under `fixtures/`).
> 3. Do this on your own phone and mailbox. Your spouse does the same for theirs.

## 1. Export SMS from the phone

Banks send alerts over SMS (not RCS), so they appear in any messaging app (Google Messages on Pixel 9a, Samsung Messages or Google Messages on Galaxy S24). The steps are the same for both phones except where noted.

### Option A: SMS Backup & Restore app (no computer needed, recommended)
1. Install **SMS Backup & Restore** from the Play Store (publisher: SyncTech Pty Ltd). Check the publisher before installing, because this app will read all your SMS.
2. Open it, grant the SMS permission it asks for. Backup does not need it to be your default SMS app; only *restore* does, and you will not restore.
3. **Set Up a Backup** → tick **Messages** only (not Call logs, not MMS) → if offered, choose *Only selected conversations* and pick your bank/card senders → destination **Your Phone** (do **not** pick Google Drive, Dropbox or email).
4. Run the backup. It writes a file like `sms-20261004123456.xml` to the phone's storage.
5. Copy that file to your computer over USB (phone shows a "File transfer" prompt) and delete it from the phone's Downloads/backup folder afterwards.

### Option B: adb over USB (no extra app, may not work on newer Android)
1. Enable Developer options: *Settings → About phone → tap **Build number** 7 times* (Galaxy S24: *About phone → Software information → Build number*). Then *Settings → System → Developer options → USB debugging* (Galaxy: *Settings → Developer options*).
2. **Galaxy S24 only:** if *Settings → Security and privacy → Auto Blocker* is on, it can block USB commands and sideloading. Turn it off for the export, then back on.
3. Install Android platform-tools on your computer, connect the phone, accept the USB debugging prompt, then:
   ```bash
   adb shell content query --uri content://sms/inbox --projection address:date:body > sms-dump.txt
   ```
4. If you see a permission error, Android is blocking shell access to SMS on your version. Use Option A instead.
5. Turn USB debugging off again afterwards.

## 2. Export emails

Use your **personal** Gmail on a computer, not the shared sync account.

### Option A: a handful of samples (recommended to start)
Open a transaction alert or statement email → ⋮ menu → **Download message**. This saves a `.eml` file. Pick one of each kind: debit alert, credit alert, card spend, card statement, EMI/loan notice, refund.

### Option B: many emails at once
1. In Gmail, search your senders, e.g. `from:(hdfcbank.net OR icicibank.com OR axisbank.com) newer_than:180d`, select all, and apply a new label `fin-samples`.
2. Go to [Google Takeout](https://takeout.google.com) → *Deselect all* → tick **Mail** → *All Mail data included* → choose only the `fin-samples` label → export as `.mbox`.
3. Download the archive from Takeout and unzip it. Delete the Takeout archive from Google afterwards.

PDF statements and other attachments are not read by the script (the app handles statements in M2).

## 3. Redact locally

Requires Python 3.9+. No packages to install.

```bash
python3 tools/redact/redact.py --input ~/exports/sms-20261004123456.xml ~/exports/alerts.mbox --out ./redacted-out
```

Accepted inputs: SMS Backup & Restore `.xml`, adb dump `.txt`, `.jsonl`, `.eml`, `.mbox`, or a folder of them.

| Option | Meaning |
|---|---|
| `--amounts balances` (default) | Keep transaction amounts, randomise balances, limits and dues |
| `--amounts all` | Randomise every amount (safest; parser tests still work) |
| `--amounts keep` | Leave all amounts |
| `--keep-words file.txt` | Extra merchant/org names (one per line) that must **not** be masked as person names |
| `--keep-nonfinancial` | Don't drop messages without transaction keywords |

What it does:
- **Drops** OTP/PIN/CVV messages, messages from personal phone numbers, and non-financial messages.
- **Replaces** links, emails, UPI IDs, PAN, phone numbers, account/card fragments (`XX1234`), bank references (UTR/RRN) and any 9+ digit number with fake values of the **same shape**. The same real fragment maps to the same fake one within a run, so "same account" logic can still be tested.
- **Masks** person names after `to / from / by`, in greetings (`Dear …`) and in `UPI/…/NAME/…` narrations. Known banks and merchants are kept.
- Reduces dates to the day, keeps only the sender header (e.g. `HDFCBK`) or the email domain.
- The random key used for fake values is never saved, so redaction cannot be reversed.

Outputs in `./redacted-out/`:
- `redacted.jsonl`: one message per line, with `review_flags` per message.
- `REVIEW.md`: stats, and every flagged message for a closer look.

## 4. Review (mandatory)

The tool is heuristic. It cannot reliably tell a person from a merchant, and it cannot see addresses or free-text notes.
1. Read **every** line of `redacted.jsonl`, not only the flagged ones.
2. Hand-edit anything that still identifies you or someone else, and mask any merchant you consider private.
3. Keep 3 to 5 examples per message type per bank (debit, credit, card spend, EMI, refund, reversal, statement).
4. Then share them (paste in chat, or commit under `fixtures/` yourself). Delete `redacted-out/` and the raw exports once done.

## 5. Tests

```bash
python3 -m unittest discover -s tools/redact -v
```
Tests use invented messages only.
