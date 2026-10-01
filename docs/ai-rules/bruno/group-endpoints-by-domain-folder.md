# Group Bruno requests by API domain folder

Each top-level folder under `bruno/ord-api/` maps to one API domain — the same boundaries used in Swagger tags and package layout (`core/auth`, `core/user`, `features/conversation`, …). Use **kebab-case** directory names without numeric prefixes.

## Good

```
bruno/ord-api/
  auth/
  users/
  conversations/
  words/
```

## Bad

```
bruno/ord-api/
  01-auth/              # numbered prefix — legacy, do not add new domains this way
  08-conversations/
```
