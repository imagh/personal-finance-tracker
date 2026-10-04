"""Synthetic-data tests. All messages here are invented. Run: python3 -m unittest discover -s tools/redact"""
import json, tempfile, unittest
from pathlib import Path
import redact as R

DEBIT = "Rs.1,250.00 debited from A/c XX1234 on 12-10-26 to VPA ramesh.k@okhdfc (UPI Ref No 628712345678). Avl Bal Rs.45,678.90. Not you? Call 18001234567"
CARD = "Rs 899.00 spent on Card XX4321 at SWIGGY on 13-Oct. Total outstanding Rs 12,345.67. Dear Anita Sharma, thank you."


class RedactTests(unittest.TestCase):
    def setUp(self):
        self.p = R.Pseudo()

    def test_identifiers_removed_amount_kept_balance_changed(self):
        t = R.redact_text(DEBIT, self.p)
        for secret in ("ramesh", "628712345678", "XX1234", "45,678.90", "18001234567"):
            self.assertNotIn(secret, t)
        self.assertIn("Rs.1,250.00", t)
        self.assertRegex(t, r"Avl Bal Rs\.\d\d,\d{3}\.\d\d")
        self.assertRegex(t, r"A/c XX\d{4}")

    def test_amounts_all_changes_txn_amount(self):
        self.assertNotIn("1,250.00", R.redact_text(DEBIT, self.p, amounts="all"))

    def test_same_fragment_same_pseudonym_within_run(self):
        a = R.redact_text("Card XX4321 used", self.p)
        b = R.redact_text("Payment received on card XX4321", self.p)
        self.assertEqual(a.split()[1], b.split()[-1])

    def test_names_masked_merchants_kept(self):
        t = R.redact_text(CARD, self.p)
        self.assertNotIn("Anita", t)
        self.assertIn("SWIGGY", t)
        self.assertNotIn("12,345.67", t)
        self.assertIn("Dear PERSON1", t)
        t2 = R.redact_text("Rs 500 sent to Ramesh Kumar On 12-10-26", self.p)
        self.assertNotIn("Ramesh", t2)
        self.assertIn("On 12-10-26", t2)
        self.assertIn("to Zomato Ltd", R.redact_text("to Zomato Ltd", self.p))

    def test_upi_narration_name_masked(self):
        t = R.redact_text("UPI/P2A/628712345678/RAMESH KUMAR/HDFC Bank/Payment", self.p)
        self.assertNotIn("RAMESH", t)
        self.assertIn("HDFC Bank", t)
        self.assertIn("PERSON1", t)

    def test_greeting_generic_kept(self):
        self.assertIn("Dear Customer", R.redact_text("Dear Customer, your bill", self.p))

    def test_url_pan_email(self):
        t = R.redact_text("see https://bank.example/x?id=1 PAN ABCDE1234F mail a.b@c.com", self.p)
        self.assertNotIn("bank.example", t)
        self.assertNotIn("a.b@c.com", t)

    def test_pipeline_filters(self):
        recs = [
            dict(channel="sms", sender="VM-HDFCBK-S", date="2026-10-12", text=DEBIT),
            dict(channel="sms", sender="AD-SBIINB", date="2026-10-12", text="Your OTP is 123456"),
            dict(channel="sms", sender="+919812345678", date="2026-10-12", text="debited please pay"),
            dict(channel="sms", sender="VM-SHOPPE", date="2026-10-12", text="Big sale on shoes!"),
        ]
        out, stats = R.process(recs)
        self.assertEqual(len(out), 1)
        self.assertEqual(out[0]["sender"], "HDFCBK")
        self.assertEqual((stats["dropped_otp"], stats["dropped_personal_sender"], stats["dropped_nonfinancial"]), (1, 1, 1))

    def test_readers(self):
        with tempfile.TemporaryDirectory() as d:
            d = Path(d)
            (d / "s.xml").write_text('<smses><sms address="VM-HDFCBK-S" date="1760270400000" body="Rs 5 debited from A/c XX1111"/></smses>')
            (d / "a.txt").write_text("Row: 0 address=AD-SBIINB, date=1760270400000, body=Rs 7 debited\nline two\nRow: 1 address=VM-ICICIB, date=1760270400000, body=Rs 9 credited")
            (d / "m.eml").write_text("From: Alerts <alerts@hdfcbank.net>\nSubject: Txn alert\nDate: Mon, 12 Oct 2026 10:00:00 +0530\nContent-Type: text/html\n\n<p>Rs 5 debited from A/c XX1111</p>")
            recs = list(R.read_any(d))
            self.assertEqual(len(recs), 4)
            self.assertIn("line two", [r for r in recs if r["sender"] == "AD-SBIINB"][0]["text"])
            self.assertEqual([r for r in recs if r["channel"] == "email"][0]["sender"], "*@hdfcbank.net")

    def test_cli_writes_outputs(self):
        with tempfile.TemporaryDirectory() as d:
            src = Path(d) / "s.xml"
            src.write_text(f'<smses><sms address="VM-HDFCBK-S" date="1760270400000" body="{DEBIT}"/></smses>')
            self.assertEqual(R.main(["--input", str(src), "--out", str(Path(d) / "o")]), 0)
            row = json.loads((Path(d) / "o" / "redacted.jsonl").read_text())
            self.assertNotIn("628712345678", row["text"])


if __name__ == "__main__":
    unittest.main()
