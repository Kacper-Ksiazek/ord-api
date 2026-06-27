# Push immediately after approved commits

`.clinerules` requires that once commits are successfully created (with user approval), they are pushed to the remote right away. Do not leave approved commits sitting only on the local branch.

## Good

```bash
# After the user approved and the commit succeeded:
git commit -m "fix(conversation): correct SSE completion signal"
git push origin main
```

## Bad

```bash
# Commit is created but never pushed, leaving remote out of sync.
git commit -m "fix(conversation): correct SSE completion signal"
# ...agent moves on to other work without pushing
```
