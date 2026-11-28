# Transactions

Status: **You can add and edit a transaction** (plan slice C, steps C4a and C4b). The Transactions tab shows a
month of them, the "+" button adds one, and tapping a row opens it for editing. The items editor (C4c), the
detail screen with history and delete (C5a), search (C5b), filters (C5c) and sort (C5d) come in the next
changes, and Home becomes a dashboard in C4d. PRD: L-1 (every transaction has items), L-5 (transfers are not
income or expense), L-9 (every transaction belongs to one account). Plan:
[2026-11-18-m1a-slice-c-transactions.md](../plans/2026-11-18-m1a-slice-c-transactions.md), amended
2026-11-25.

This page grows with each change. Today it describes the Transactions tab and the add and edit form.

## The Transactions tab

It is the second tab in the bar at the bottom: **Home, Transactions, Accounts, More**. It lists one month of
your transactions at a time.

- **The month strip.** A row of months under the title, scrolling sideways. The month you are looking at is
  **underlined and bold**; the year is written under the first month and under every January. It lists every
  month from your first transaction to your latest, and always includes this month. Tap a month to see it.
  You can also **swipe the list sideways** to step to the previous or next month. Going forward past today is
  allowed: the next month is simply empty.
- **The totals banner.** Three lines for the month you are looking at: **Expense** (shown with a minus),
  **Income** (shown with a plus, in green) and **Net**, which is income minus expense. A net below zero is
  shown with a minus and is not red: only a negative account balance or a budget you have gone over is red.
  A transfer between your own accounts is in none of the three.
- **The days.** Under the banner, each day that has transactions is a block, newest first: the day is
  written "Today", "Yesterday" or as a date, with that day's net total at the end of its heading, and its
  transactions are listed inside it.
- **A transaction row** shows a tile for its category, the title, the category and the account ("Groceries
  · HDFC"), small tags for any tags it has, and the amount at the end, with a minus for an expense and a plus
  (in green) for income.
  - A transaction split into several items shows a receipt tile and "3 items" instead of one category.
  - A transfer shows a transfer tile and "HDFC → Axis Card", and its amount has no sign.
  - A transaction with no title shows its category's name instead (or "Expense" or "Income" if it has no
    single category).
  - An entry that came in automatically and that you have not confirmed shows a **Needs review** label.
- **An empty month** says "No transactions this month" under the banner, with the totals at zero.
- **Your month is remembered.** If you look at September, go to Accounts and come back, the tab is still on
  September. It reads the month again each time you return, so something you added elsewhere is there.
- **Times follow your phone.** Which day or month a transaction falls on is decided in the time zone your
  phone is set to right now, so a transaction made at 23:30 on the 31st in India is in that month.

### For TalkBack

- Each month in the strip is read as its full name and year ("November 2026"), as one choice among the
  months, with the chosen one marked selected.
- Each banner line is read once, with its word first: "Expense, ₹2,050.00", "Income, ₹30,000.00", "Net,
  ₹27,950.00" or "Net, minus ₹300.00". The arrows are decoration.
- Each transaction row is read as one item: the title, the category, the account, the tags, the amount with
  its direction ("Expense, ₹486.00") and "Needs review" if it applies.

### Large text and small screens

- Amounts are never cut off. With large text the amount in a row or a banner line moves under its label.
- The tab bar still gives every tab at least 48dp. When the open tab's name does not fit beside the other
  three (the word "Transactions" at large text on a small phone), the bar shows that tab as its icon only; the
  name is still spoken.

## Adding and editing

Tap the **+** button on the Transactions tab to add a transaction; tap a row to change one. Both open the same
form, titled "New entry" or "Edit entry". The tab bar is hidden while it is open, Back leaves without saving,
and **Save** (at the bottom) writes it and returns to the tab, which shows the change and the new totals.

- **Type.** Expense, Income or Transfer. A new form starts as an expense. When you edit, an expense and an
  income can be switched into each other, but a transfer stays a transfer and an expense or income never turns
  into one.
- **Amount.** In rupees, up to two decimals (grouping like 1,00,000 is fine). It must be more than zero and
  not more than the limit. The sign comes from the type, so you never type a minus.
- **Account.** Required: pick it from your active accounts. For a transfer there are two, **From account** and
  **To account**, and they must be different.
- **Date.** Opens a calendar. A new transaction takes the time of day it was opened at, and changing the date
  keeps that time. When you edit, the stored time is kept; the date you see is in your phone's time zone.
- **Title, note and bank reference** are all optional; leave them empty and nothing is stored. A title is
  at most 200 characters, a note 2,000 and a bank reference 64. A transaction with no title shows its category
  in the list.
- **Category and tags** (not for a transfer's category). A category is optional, and only categories of the
  type's kind are offered. Switching expense to income (or back) removes a category of the other kind and says
  "Category cleared: it does not fit the new type". Tags are optional; a transfer can have them too.
- **A transfer** has no category and no bank reference. Saving writes both sides of it together, and editing it
  changes both sides together.
- **What stays as it was.** A category, tag or account that was archived after you made the transaction stays
  on it when you change something else (for example the title). You can still pick a different one.
- **Mistakes.** Every wrong field shows its own message at once and nothing is saved ("Enter a valid amount",
  "Required", "At most 200 characters", "Choose a different account"). If saving fails for another reason the
  form says "Couldn't save. Try again." and keeps what you typed.
- **One save.** Tapping Save twice quickly saves once.
- **A transaction that is gone.** If the one you tapped was deleted meanwhile, the tab says "That transaction no
  longer exists".
- **Every change is recorded** in the audit history with who made it and on which device; an edit that changes
  nothing records nothing.

### For TalkBack

- Account, category, tags and date are each one button that says its name and its value together ("Account,
  HDFC", "Date, Nov 15, 2026"); an error on one is announced with it. Type is one group of choices.
- The "category cleared" notice and the failure message are announced when they appear, without moving focus.
- Every field is at least 48dp tall.

### What is not here yet

A transaction with more than one item cannot be opened in this form yet (the items editor arrives in C4c), so
tapping one shows "That transaction no longer exists" instead of showing half of it. There is no way to remove
a category from a transaction once picked (switch the type, or pick another). Deleting, the history, marking
verified and the detail screen arrive in C5a, search, filters and sort in C5b to C5d, and the Home shortcuts in
C4d. The settings sheet for the banner (roadmap R-7) is deferred.
