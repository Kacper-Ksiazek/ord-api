# Pushing to main publishes the API types package

`.github/workflows/publish-api-types.yml` runs on every push to `main` that touches `src/main/kotlin/**/*.kt` or `pom.xml`. It boots the app, exports the OpenAPI spec, and — if the spec changed — generates TypeScript types and publishes a new `@ord-api/ord-api-types` NPM version plus a GitHub release. Treat any merge to `main` that changes Kotlin endpoints/DTOs as a public type release.

## Good

```text
Before merging endpoint/DTO changes to main:
- Confirm the OpenAPI changes are intentional and backward-compatible for frontend consumers.
- Expect a new @ord-api/ord-api-types@1.0.<run_number> to be published automatically.
```

## Bad

```bash
# Renaming a DTO field as a "quick rename" and pushing to main,
# unaware it auto-publishes a breaking types-package version to NPM.
git push origin main
```
