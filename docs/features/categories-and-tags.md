# Categories and tags

Status: **Categories: data and rules built, no screens yet** (plan slice B3a core, B3b storage and
first-run seeding). Tags and every screen (the category list, the create and edit form, the picker, emoji
icons, the tags list) arrive in later PRs of slice B (B4a to B4c, B5a, B5b). PRD: L-2 (categories), L-3
(tags). Plan: [2026-11-10-m1a-slice-b-ledger-setup.md](../plans/2026-11-10-m1a-slice-b-ledger-setup.md).

This page grows with each PR. Today it describes what a category is, the list you start with, and the
rules the app already enforces underneath. You cannot see or change categories yet: the screens come next.

## What a category is

A category groups the items of a transaction: "Food & dining", with sub-categories such as "Groceries"
and "Eating out". Later, every item of a transaction will carry a category, budgets will limit them, and
reports will add them up.

- **Two levels only.** A category is either top-level or a sub-category of a top-level one. A
  sub-category cannot have its own sub-categories.
- **Expense or income.** Each category is one or the other, and a sub-category has its parent's kind. The
  expense and income trees never mix.
- **A name, a colour and an optional icon.** The name is 1 to 100 characters (spaces around it are dropped).
  The colour is one of 12 fixed colours. The icon is a symbol from the app's set or a single emoji, or none
  (then the first letter of the name is shown).
- **Order.** Categories are shown in the order you give them. A new one goes last among its siblings.
- **Every create, edit, archive and delete is recorded** in the audit trail, with who did it and from
  which phone. The starter categories are recorded as made by the system, attributed to you and your phone.

## The categories you start with

First-run setup (see [first-run-and-profiles.md](first-run-and-profiles.md)) creates these 31 categories in
the same step as your profile. They are ordinary categories: rename, recolour, re-icon, archive or delete
them as you like. The exception is **Loan EMI**, a system category (below).

| Category | Kind | Sub-categories | Colour |
|---|---|---|---|
| Food & dining | Expense | Groceries, Eating out, Food delivery | coral |
| Transport | Expense | Fuel, Cab & auto, Public transport | sky |
| Shopping | Expense | none | magenta |
| Bills & utilities | Expense | Electricity, Water, Internet & mobile, Subscriptions | amber |
| Home | Expense | Rent, Maintenance & repairs, Household help | olive |
| Health | Expense | Medicines, Fitness | rose |
| Entertainment | Expense | none | violet |
| Travel | Expense | none | teal |
| Education | Expense | none | indigo |
| Personal care | Expense | none | emerald |
| Gifts & donations | Expense | none | magenta |
| Loan EMI (system) | Expense | none | slate |
| Salary | Income | none | emerald |
| Interest | Income | none | teal |
| Refunds & cashback | Income | none | sky |
| Other income | Income | none | slate |

That is 16 top-level categories and 15 sub-categories, 27 expense categories (counting Loan EMI) and 4
income categories. Each has a symbol icon (for example the restaurant symbol for Food & dining); the symbols are
listed in `DefaultCategories` in core. The list lives in code, with a permanent key for each entry:

- **A default you delete stays deleted.** The keys give each default the same id on every phone, and
  setup creates only the ones that do not exist yet (deleted ones count as existing), so a default you
  removed is never put back, and two phones will not end up with two "Food & dining" (M2 sync).
- **Phones set up before this update do not get the list** (an owner decision: only development installs
  existed). Only a fresh setup creates it.

## Rules the app already enforces

These live in core (`CategoryService`) and are tested there and through the real database; the screens
will show their messages as they arrive.

- **Two levels.** A sub-category's parent must be a top-level category that is not archived or deleted.
- **Kind and parent are fixed once created.** Editing changes the name, colour and icon only. To move a
  category to another parent or kind, delete it and create it again.
- **Names are unique among siblings**, ignoring case and surrounding spaces: you cannot have "Groceries"
  and "groceries" under Food & dining, but the same name under a different parent (or as an income
  category) is fine. A deleted category does not block its name; restoring one into a name that has been
  taken since is refused.
- **Archive hides, delete removes.** An archived category, and the sub-categories of an archived parent,
  no longer show up when picking a category for a new item, but stay in history; unarchiving the parent
  brings its non-archived sub-categories back. Delete is reversible (restore).
- **A parent with sub-categories cannot be deleted** until they are. A sub-category whose parent is deleted
  cannot be restored until the parent is.
- **Loan EMI cannot be archived or deleted**, because loans (a later slice) book their instalments to it. You can
  rename it, recolour it and change its icon. There is at most one.
- **Reordering** applies to the siblings of one parent and kind: the new order must list exactly the
  current ones.
- **A corrupt stored colour or icon never breaks the list**: an unknown colour is shown as slate and an unknown
  icon as the name's first letter. (The values are untrusted when they are read back, for example after
  sync.)

## Where it lives

Core: `core/ledger/.../category/` and `setup/FirstRunService` (rules, the default list, the icon grammar);
see `core/ledger/README.md`. Storage: `app/.../data/category/` and schema v3 (see
[data-model.md](../architecture/data-model.md)); `app/README.md` for the wiring. Tests: `CategoryServiceTest`
and friends in core, and `RoomCategoryIntegrationTest`, `RoomCategoryMapperTest`, `RoomCategoryForeignKeyTest`,
`MigrationTest` and the Setup tests in `app`.

## Tags

Tags (PRD L-3) are not built yet; they get their own page section here when B5 lands.
