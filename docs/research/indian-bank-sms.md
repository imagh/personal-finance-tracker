# Research: Indian bank SMS formats and sender IDs

Researched 2026-10-04 from public web sources. Purpose: decide how the SMS parser should cover "all Indian banks" (owner request).

## Findings

1. **No authoritative, current catalogue of Indian bank SMS templates exists on the web.** Banks change wording without notice. What is public is (a) sender header registries and (b) open-source parsers with regexes for the large banks.
2. **Sender header format.** Indian transactional SMS show a header like `VM-HDFCBK`, `AD-SBIINB`, `AX-AXISBK-S`. The two letters before the hyphen are an operator/route prefix, the six characters after it are the DLT-registered header, and a `-S` suffix marks a service message. The prefix and suffix vary for the same bank, so match on the middle part only.
3. **Header registry.** A public gist lists 1,000+ headers across 400+ banks, last updated December 2022, so it is stale. Banks register many headers each (SBI alone has 400+ codes, HDFC 20+, ICICI 5+). Examples: HDFC `HDFCBK`, `HDFCBN`, `HDFCCC`, `HDFCDC`; ICICI `ICICIB`, `ICBANK`, `ICICBK`; Axis `AXISBK`, `AXISB`; Kotak `KOTAKB`, `KBANKT`; SBI `SBIINB`, `SBIUPI`, `ATMSBI`; PNB `PNBSMS`, `PNBCRD`; BoB `BOBBNK`, `BOBBIZ`; India Post Payments Bank `IPBMSG`.
4. **Open-source parsers cover roughly 6 to 14 banks** (HDFC, ICICI, SBI, Axis, Kotak, BoB, PNB, Canara, Union, IDFC First, IndusInd, Federal, Yes) plus UPI apps. None claim every bank.
5. **Tricky cases the better parsers handle:** self-transfers detected from account numbers, card payments and ATM withdrawals, UPI reference position inside narration (`UPI/…/<12-digit UTR>/…`), and rejection of OTPs, promos and balance-only alerts.
6. **Spoofing is real.** Fake-sender apps and phishing messages imitate bank alerts, so SMS content must be treated as untrusted (PRD S-12).

## Recommended strategy (reflected in the PRD)

| Layer | What | Why |
|---|---|---|
| 1. Sender alias registry | A data file mapping bank to header aliases. Seeded from the sources below, matched after stripping prefix and suffix. | Cheap and extendable; no code change per bank. |
| 2. Generic parser | Bank-agnostic extraction of amount (`Rs`/`INR`), direction (debited/credited/spent/received/withdrawn), account fragment (`XX1234`), UPI/NEFT/IMPS reference, merchant, available balance. | Covers most banks with no bank-specific work. |
| 3. Per-bank overrides | Templates plus fixtures only for the banks the household really uses. | Accuracy where it matters, bounded effort. |
| 4. Parse log | Unparseable or wrongly parsed messages go to the local log (PRD S-11). | Gaps become visible instead of silently dropped. |

"All Indian banks" is therefore handled as: every bank with a registry entry is *recognised*, the generic parser *attempts* it, and only the owner's banks are *guaranteed and tested*. Anything else lands in the parse log.

## What I need from the owner

The list of banks, cards and UPI apps you and your spouse use, with a few redacted real messages each (debit, credit, card spend, EMI, refund, failed or reversed). Without real samples, the per-bank templates cannot be verified.

## Sources

- [Indian bank SMS header registry (gist, updated Dec 2022)](https://gist.github.com/abhishekjnvk/ad596df86a22291ccd76cf29af52b656)
- [SMS sender ID / TRAI header format overview](https://fivosms.co.uk/sender-id.php)
- [Technofino: bank/CC official SMS ending with -S](https://technofino.in/community/threads/bank-cc-official-sms-ending-with-s.40715/)
- [Fake SMS sender apps and spoofing, 2026 (PhishGuard)](https://www.phishguard.co.in/blog/fake-sms-sender-apps-how-bank-sms-is-spoofed-in-2026)
- Open-source parsers (reference only; check each licence before reusing any code): [hisab-kitab (MIT)](https://github.com/KrishnaMahajan10/hisab-kitab), [FinTrack](https://github.com/yatin536/FinTrack), [transaction_sms_parser](https://github.com/MabudAlam/transaction_sms_parser), [kharcha](https://github.com/AkashPriyadarshii/kharcha/releases/tag/v0.1.2)
