# Ensure smoke tests pass before merging to main

Pull requests targeting `main` run `.github/workflows/smoke-tests.yml` (`make test-smoke` with AI stubs, no OpenAI key). A failed run blocks merge once **Smoke tests** is marked as a required check in GitHub branch protection.

Pushes to `main` run the same suite first via `.github/workflows/deploy.yml` — deploy only proceeds when smoke tests pass.

## Good

```bash
# Run the same suite CI runs before opening / updating a PR.
make test-smoke
# Merge only when the GitHub "Smoke tests" check is green.
```

## Bad

```bash
# Merging with a red or skipped "Smoke tests" check.
git push origin main   # deploy is also blocked until smoke tests pass
```
