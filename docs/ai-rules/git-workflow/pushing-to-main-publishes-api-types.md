# Pushing a changed openapi.json to main publishes the API types package

`.github/workflows/publish-api-types.yml` runs on every push to `main` that changes the committed `openapi.json` (the contract / source of truth). It generates TypeScript types from that spec and publishes a new `@kacper-ksiazek/ord-api-types` version to **GitHub Packages** (`npm.pkg.github.com`). There is no GitHub release and no `NPM_TOKEN`; auth is the built-in `GITHUB_TOKEN`. Treat any merge to `main` that re-exports `openapi.json` as a public type release.

## Good

```text
Before merging endpoint/DTO changes to main:
- Re-export the spec (`make openapi`) and commit openapi.json — that commit IS the trigger.
- Confirm the OpenAPI changes are intentional and backward-compatible for frontend consumers.
- Expect a new @kacper-ksiazek/ord-api-types@1.0.<run_number> on GitHub Packages.
```

## Bad

```bash
# Changing a DTO, committing openapi.json, and pushing to main,
# unaware it auto-publishes a breaking types-package version to GitHub Packages.
git push origin main
```
