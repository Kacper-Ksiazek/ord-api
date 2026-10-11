# Use emoji labels and seq for Bruno folder ordering

Each `folder.bru` sets a human-readable `meta.name` (emoji + domain label) and a `seq` value controlling sidebar order. Domain order follows the Swagger tag numbering (`1. Core`, `2. Words`, `5. Conversations`, …) but **directory names stay unnumbered**.

## Good

```bru
meta {
  name: 💬 Conversations
  seq: 5
}
```

## Bad

```bru
meta {
  name: 08-conversations    # leaks legacy numbering into display name
  seq: 16
}
```
