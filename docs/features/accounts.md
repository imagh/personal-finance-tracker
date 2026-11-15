# Accounts

Status: **The list is built; adding and editing accounts is not yet** (plan slice B2a). Creating and
editing an account, with its opening balance, arrives in B2b, and the account picker in B2c. PRD: L-4
(accounts), L-9 (every transaction belongs to one account), INT-3 (delete is undoable). Plan:
[2026-11-10-m1a-slice-b-ledger-setup.md](../plans/2026-11-10-m1a-slice-b-ledger-setup.md).

This page grows with each PR. Today it describes what an account is and what you can do with the list.

## What an account is

An account is a place your money is kept: where each transaction (from slice C) will be recorded.

- **Four types**, each with its own badge and colour: **Bank** (blue bank building), **Credit card**
  (violet card), **Cash** (green banknotes) and **Wallet** (teal wallet). You never pick an icon.
- **A name** of 1 to 100 characters. Names do not have to be unique: two cards from one bank are normal.
- **Optional details:** the **last four digits** of the account or card number, and the **issuer** (the
  bank or provider). Under each account's name the list shows them as "Bank · HDFC · ••1234"; whatever is
  missing is left out.
- **The currency is always INR.**
- **No stored balance.** An account's balance will be its opening balance plus its transactions, worked out
  when you look at it (INT-6), once transactions exist.
- **Every create, edit, archive, delete and undo is recorded** in the audit trail, with who did it and from
  which phone.

## The accounts list

Open it from **Home → More (the three-dots button) → Accounts**. Back returns to More, and Back again to
Home.

- **Active accounts** are listed by name (ignoring capital letters), then in the order they were created.
- **Archived accounts** are under a line that says **"Archived (2)"** (the number is how many). It starts
  collapsed; tap it to expand or collapse it. TalkBack says whether it is expanded.
- **No accounts yet?** The screen says "No accounts yet". (There is no Add button until the account form
  arrives in the next update.)
- **Each account has a three-dots button** ("Actions for HDFC Savings" for TalkBack) that opens a menu:
  - **Archive** (on an active account) moves it to the Archived section.
  - **Unarchive** (on an archived account) moves it back.
  - **Delete** (on either) removes it from the list.

## Archive or delete?

- **Archive** is for an account you no longer use but want to keep for history. It stays in the list,
  under Archived, and can come back with Unarchive. Archived accounts will not be offered when you record
  a new transaction.
- **Delete** is for a mistake or something you never want to see. The account disappears from every
  list. It is not erased: the app keeps it as a deleted record in the audit trail, which is what lets
  Undo bring it back.

## Undo

After a delete, a message at the bottom says **"Account deleted"** with **Undo**. Tap Undo within about
ten seconds and the account is back in the list, exactly as it was. The message goes away, and the
delete stands, when the time runs out, when you swipe it away, or when you leave the screen or the
phone's screen goes off. Only the latest delete can be undone.

(There is not yet a way to restore a deleted account after the message has gone; that is deliberately
left for a later change, and nothing is erased in the meantime.)

## If something goes wrong

If archiving, unarchiving, deleting or undoing fails unexpectedly (for example the phone is out of
storage), a message says **"Couldn't archive. Try again."** (or "unarchive", "delete", "undo") and the
list stays exactly as it was. Nothing is half-done: each action is one saved step.

If an account was changed from somewhere else in the meantime, the list simply refreshes.

## Privacy

Account names, issuers and the last four digits are shown only on the screen. They are never written to
the app's logs: a log line says only that an action on an account failed, with the action's name.

## Configuration

None. The limits (name length, last four digits) are fixed rules in the core module.
