# Transactions

Status: **The Transactions tab is built** (plan slice C, step C4a). You can look at a month of
transactions, but not yet add, open or change one: the add form (C4b), the items editor (C4c), the detail
screen with history and delete (C5a), search (C5b), filters (C5c) and sort (C5d) come in the next changes,
and Home becomes a dashboard in C4d. PRD: L-1 (every transaction has items), L-5 (transfers are not income or
expense), L-9 (every transaction belongs to one account). Plan:
[2026-11-18-m1a-slice-c-transactions.md](../plans/2026-11-18-m1a-slice-c-transactions.md), amended
2026-11-25.

This page grows with each change. Today it describes the Transactions tab.

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

### What is not here yet

There is no add button, and tapping a row does nothing: both arrive with the add form (C4b) and the detail
screen (C5a). The search, filter and sort buttons arrive with C5b to C5d, and the settings sheet for the banner
(roadmap R-7) is deferred.
