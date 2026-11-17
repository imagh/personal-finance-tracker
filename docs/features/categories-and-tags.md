# Categories and tags

Status: **Categories: the list and the create/edit form are built** (More, then Categories: browse, reorder,
archive, delete with Undo, add a category or sub-category, edit one). Emoji icons come in B4c; the category
picker is built (B4b) and waits for the transaction form (C4b) to use it. Tags: data and rules built (B5a), no
screens yet (B5b). PRD: L-2 (categories), L-3 (tags). Plan:
[2026-11-10-m1a-slice-b-ledger-setup.md](../plans/2026-11-10-m1a-slice-b-ledger-setup.md).

This page grows with each PR. Today it describes what a category is, the list you start with, what the
categories list does, how you create and edit a category, and the rules the app enforces underneath.

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

## The categories list

Open **More, then Categories**. A switch at the top chooses **Expense** (shown first) or **Income**; the
list below shows only that kind.

- **Rows.** Each top-level category is followed by its sub-categories, inset a step, in the order you
  gave them. Each row shows its icon (a symbol, an emoji, or the first letter of its name when it has
  none) in its colour, and its name. A row's **three-dot menu** holds its actions.
- **Reorder: Move up and Move down.** There is no dragging. Move up and Move down swap a category with
  the one next to it among its siblings: top-level categories move among top-level ones, and a
  sub-category moves among the sub-categories of the same parent. The first row of a group has no Move
  up and the last has no Move down. Archived siblings keep their place, so a move skips over them. Two
  categories that ended up with the same position (for example after a restore) still move.
- **Archive and Unarchive.** Archive moves a category to the collapsed **Archived (n)** section at the
  bottom, and hides it from pickers. Archiving a parent moves its sub-categories there with it (they
  show under it, inset); they offer only Delete, because they are hidden by their parent rather than
  archived themselves. A sub-category you archive on its own appears in that section with "Under
  <parent>" beneath its name. Unarchive puts it back where it was.
- **Delete, with Undo.** Delete removes a category and shows "Category deleted" with **Undo** for about
  10 seconds. Undo restores it, in its old place. Only the latest delete can be undone, and leaving the
  screen ends the offer (the delete stands).
- **A parent with sub-categories cannot be deleted.** Choosing Delete shows "Delete its sub-categories
  first" and changes nothing; delete or archive-and-delete its sub-categories first.
- **Loan EMI** can be moved but its menu has no Archive and no Delete.
- **If something goes wrong** (a save fails), a short fixed message such as "Couldn't archive. Try again."
  appears and the list is left as it was. The message never contains a name.
- **Add and edit.** The **+** button (and the "Add category" button on an empty list) opens the form for a
  new category of the kind on show, and tapping a row, active or archived, opens that category for editing
  (see below). If the category was deleted elsewhere while you were looking at the list, you come back to
  the list with "That category no longer exists".
- A fresh setup already has the starter list; phones set up before slice B have no categories until you
  add them.

Nothing on this screen logs a name: failures log only the kind of record and the action.

## Creating and editing a category

Tap **+** on the categories list (or a row, to edit). The form has:

- **Name.** Required, up to 100 characters. If another category with the same parent and kind already has
  that name (ignoring capitals and surrounding spaces) the form says "Already in use" under the field and
  saves nothing. The same name under a different parent is fine.
- **Type.** Expense or Income. It is the kind on show in the list when you tapped **+**, and it is only
  shown here: you cannot change it. To make an income category, switch the list to Income first.
- **Parent category.** "None (top level)" makes a top-level category. Tap the row to make a sub-category
  instead: a sheet "Choose parent category" lists only the top-level categories of that kind that are not
  archived or deleted (never a sub-category, so there are never three levels). Pick one and the row shows its
  name; "Make top level" clears it again.
- **Colour.** Twelve colour swatches; the chosen one has a check mark and a ring. A new category starts
  slate, the neutral colour.
- **Symbol.** A grid of the app's category symbols, drawn in the chosen colour, with "No symbol" first
  (the category then shows the first letter of its name, which you see change as you type). A new category
  starts with no symbol. (Emoji icons arrive with B4c.)

**Editing** shows the same form with everything filled in. You can change the name, the colour and the symbol.
**The type and the parent are shown but fixed**, each with "Can't be changed after the category is created":
to move a category or change its kind, delete it and create it again. The system category Loan EMI can be edited like any other.
A category with an emoji icon keeps it until you pick a symbol. Saving without changing anything writes
nothing.

Behaviour worth knowing:

- Save writes one audited change (who and from which phone, as for every record). If saving fails
  unexpectedly, "Couldn't save. Try again." appears, what you typed is kept, and nothing was written; only
  the kind of record is logged, never the name.
- A second tap on Save while it is saving is ignored.
- If the category was deleted after you opened it, you return to the list with "That category no longer
  exists".
- **Screen readers.** The type and parent rows are read as a label and a value. Every colour is read by its
  name ("Amber") and every symbol by its name ("Local cafe"), as one group of choices with the chosen one
  announced as selected. In the parent sheet each category is read once by name; a sub-category is followed by
  "Under <parent>", so two sub-categories called "Misc" are told apart.

The route (`categories/edit`) carries only ids and enum names, which the form treats as untrusted: an
unknown or malformed category, parent or kind returns you to the list with the "no longer exists" message.

### The category picker

The same parent sheet, `CategoryPicker`, is what the transaction form (C4b) will use to choose an item's
category. It offers only categories that can be used now: an archived category, and the sub-categories of an
archived parent, are not listed, and the order is the list's. Each top-level category and its sub-categories
sit together; tapping one chooses it and closes the sheet once. It logs nothing.

## Rules the app already enforces

These live in core (`CategoryService`) and are tested there and through the real database; the screens
show their messages through the form above.

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
  current ones, archived siblings included. Core hands out positions itself and breaks ties between equal
  positions, so a swap always takes effect.
- **A corrupt stored colour or icon never breaks the list**: an unknown colour is shown as slate and an unknown
  icon as the name's first letter. (The values are untrusted when they are read back, for example after
  sync.)

## Where it lives

Core: `core/ledger/.../category/` and `setup/FirstRunService` (rules, the default list, the icon grammar);
see `core/ledger/README.md`. Storage: `app/.../data/category/` and schema v3 (see
[data-model.md](../architecture/data-model.md)); `app/README.md` for the wiring. Tests: `CategoryServiceTest`
and friends in core, and `RoomCategoryIntegrationTest`, `RoomCategoryMapperTest`, `RoomCategoryForeignKeyTest`,
`MigrationTest` and the Setup tests in `app`. The list: `ui/categories/` in `app`, with
`CategoryService.lists(kind)` in core; tests `CategoryListsTest` (core), `CategoriesViewModelTest`,
`CategoriesActionsTest`, `CategoryRowMapperTest`, `CategoriesScreenTest`, `CategoriesFlowTest` and the
`CategoriesScreenshotTest` goldens in `app`. The form and picker: `ui/categories/` (`CategoryEditViewModel`,
`CategoryEditScreen`, `SymbolGrid`, `ColourSwatches`) and `ui/common/CategoryPicker.kt` in `app`; tests
`CategoryEditViewModelTest`, `CategoryEditScreenTest`, `SymbolGridTest`, `ColourSwatchesTest`,
`CategoryPickerTest`, `CategoriesRouteTest`, `CategoryRoutesTest`, `LaunchSaveTest`, the form paths of
`CategoriesFlowTest`, and the `category-edit-*`, `category-symbol-grid-*`, `category-swatches-*` and
`category-picker-*` goldens.

## Tags

Tags (PRD L-3): the data and rules are built (B5a) but nothing shows them yet; this section is written with the tags screens in B5b.
