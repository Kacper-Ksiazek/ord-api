# Never commit without explicit user approval

Per `.clinerules` and [`concept/agent-behavior.md`](../concept/agent-behavior.md), do not run
`git commit` on your own. When changes are complete, stop, summarize what changed, and ask
the user whether to commit. Only commit after explicit permission (Cloud Agent defaults included).

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
