# Never commit without explicit user approval

Per `.clinerules`, an agent must NEVER run `git commit` on its own initiative. When changes are complete, stop, present a concise summary of what changed, and explicitly ask the user whether to commit. Only run git commands after receiving explicit permission.

## Good

```bash
# 1. Finish the changes, then show the user what would be committed.
git status
git diff --stat

# 2. Summarize in chat and ask:
#    "Here is what I changed: ... Do you want me to commit these?"
# 3. Wait for an explicit "yes" BEFORE running any git command.
git commit -m "feat(words): add bulk import endpoint"   # only after approval
```

## Bad

```bash
# Agent finishes edits and immediately commits without asking.
git add -A
git commit -m "stuff"   # no summary, no user approval — violates .clinerules
```
