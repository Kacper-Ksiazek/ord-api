# Re-export the OpenAPI spec after changing request/response DTOs

`openapi.json` is the source the frontend `types-package` is generated from. After changing any request/response DTO or controller signature, re-export the spec with `make openapi` (against a running app) so `openapi.json` stays in sync. CI compares the freshly exported spec against the last release, so a stale local spec just hides changes from review.

## Good

```bash
# With the app running locally (e.g. make docker-restart):
make openapi          # runs ./export-openapi-spec.sh -> openapi.json
git diff openapi.json # review the contract change before committing
```

## Bad

```bash
# Changed a response DTO but left openapi.json untouched.
# The committed contract no longer matches the API, so reviewers
# and the generated TypeScript types drift out of sync.
git add src/main/kotlin/.../WordResponse.kt
git commit -m "add field to WordResponse"   # openapi.json not re-exported
```
