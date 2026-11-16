# Accounts

Status: **The list, adding, editing and the account picker are built** (plan slices B2a, B2b and B2c). The
picker is not on any screen yet: the transaction form (slice C) is its first user. PRD: L-4
(accounts), L-9 (every transaction belongs to one account), INT-3 (delete is undoable). Plan:
[2026-11-10-m1a-slice-b-ledger-setup.md](../plans/2026-11-10-m1a-slice-b-ledger-setup.md).

This page grows with each PR. Today it describes what an account is, the list, the add and edit form, and the account picker.

## What an account is

An account is a place your money is kept: where each transaction (from slice C) will be recorded.

- **Four types**, each with its own badge and colour: **Bank** (blue bank building), **Credit card**
  (violet card), **Cash** (green banknotes) and **Wallet** (teal wallet). You never pick an icon.
- **A name** of 1 to 100 characters. Names do not have to be unique: two cards from one bank are normal.
- **Optional details:** the **last four digits** of the account or card number, and the **issuer** (the
  bank or provider). Under each account's name the list shows them as "Bank · HDFC · ••1234"; whatever is
  missing is left out.
- **The currency is always INR.**
- **An opening balance**, the amount you start tracking from (see "The opening balance" below).
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
- **No accounts yet?** The screen says "No accounts yet", with an **Add account** button.
- **Add** with the round **+** button at the bottom (labelled "Add account" for TalkBack), or the Add
  account button when the list is empty. **Tap an account** to edit it, active or archived.
- **Each account has a three-dots button** ("Actions for HDFC Savings" for TalkBack) that opens a menu:
  - **Archive** (on an active account) moves it to the Archived section.
  - **Unarchive** (on an archived account) moves it back.
  - **Delete** (on either) removes it from the list.

## Adding and editing an account

The form has these fields, in this order. Back returns to the list without saving; there is no "discard
changes?" question.

- **Name** (required, up to 100 characters).
- **Type**: Bank, Credit card, Cash or Wallet (Bank to start with).
- **Last 4 digits** (optional): exactly four digits `0-9`, or empty. Digits of other scripts are refused.
- **Issuer** (optional): up to 100 characters, or empty.
- **Current balance**, or **Amount owed** when the type is Credit card (see below).

Press **Save**. If any field is wrong, **every** wrong field shows its own message under it at once
("Required", "At most 100 characters", "Enter exactly 4 digits", "Enter a valid amount") and nothing is
saved. What you typed is never changed or cut short: the app tells you what is wrong instead.

### The opening balance

You type the amount as a plain positive number, in rupees: `5000`, `5,000`, `1,23,456.78` or `1,234,567.89`
all work, with at most two decimals, up to Rs 10,000,00,00,000. A minus sign, a third decimal place, letters
and misplaced commas are refused. The field cannot be left empty: type `0` for none.

- **Bank, Cash and Wallet** ask for the **Current balance** and keep it as you typed it.
- **Credit card** asks for the **Amount owed** and stores it as a negative balance, because money you owe
  counts against you. Owing Rs 5,000 on a card is saved as minus Rs 5,000.
- Changing the type after typing changes the label and the sign it is saved with, not the digits you typed.
- When you edit an account that owes money, the form shows the owed amount as a positive number again
  ("5,000.00" under "Amount owed").
- If the balance and the type are left untouched while you edit something else, the balance is saved exactly
  as it was. (An overdrawn bank account cannot be entered in this form, but it can exist later, for example
  from an import; renaming it will not turn the overdraft into money in the bank.)

Saving with nothing changed writes nothing to the audit trail.

### If the account is gone

If you tap an account that has been deleted in the meantime, the form does not open: you stay on the list,
which says **"That account no longer exists"** and refreshes.

### If saving fails

A message under the fields says **"Couldn't save. Try again."**, the Save button works again and what you
typed is kept. Nothing is half-saved.

## Choosing an account (the account picker)

Wherever the app asks "which account?" (first the transaction form, later budgets and loans) it opens
the same bottom sheet, **Choose account**. It is not on a screen yet; the transaction form will be its
first user.

- **Only active accounts are listed.** Archived and deleted accounts never appear, so a new transaction
  cannot be put on one. They are sorted by name ignoring capital letters, like the list.
- **Each row shows** the type badge (the same colours as the list; TalkBack says the type, for example
  "Bank"), the name, and under it the type, issuer and last four digits ("Bank · HDFC · ••1234"), so two
  accounts with similar names can be told apart.
- **The current account is marked** with a check mark, and TalkBack says it is selected. If the current
  account has since been archived it is simply not in the list, and nothing is marked.
- **Tap an account** to choose it: the sheet slides away with that account chosen. A second tap while it
  slides away does nothing.
- **Back, a tap outside the sheet, or a swipe down** closes it without choosing anything.
- **No accounts yet?** The sheet says "No accounts yet. Add one from More."

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
the app's logs: a log line says only that an action on an account failed, with the action's name, or that
saving an account failed (`entity_save_failed kind=ACCOUNT`). Amounts are never logged either.

## Configuration

None. The limits (name length, last four digits) are fixed rules in the core module.
