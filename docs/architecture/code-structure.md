# Personal Finance Tracker — Code structure

The module/package map — kept current as modules are added or responsibilities move
(CLAUDE.md Rule B). For each entry: what it owns and its one-line responsibility.

## Layout

```
core/
  <module>/            # <one-line responsibility>
    README.md          # module doc (Purpose · Boundaries · Interface · Data · Config · Deps · Run/test)
    ...                # controller/handler → service → dao → db within the module
core/shared/
  <package>/           # shared, versioned code (promoted on the 2nd consumer — never copied)
```

## Modules

| Module | Owns | Responsibility |
|--------|------|----------------|
| <name> | <models/tables/adapters> | <what it does> |

## Shared packages

| Package | Provides | Consumers |
|---------|----------|-----------|
| <name>  | <what>   | <modules> |
