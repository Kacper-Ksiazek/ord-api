# Ensure `AllTestsSuite` passes before pushing to main

A push to `main` triggers `.github/workflows/deploy.yml`, which runs `com.ord.AllTestsSuite` and then builds and deploys the Docker image to Heroku. A red suite blocks the deploy, so run the full suite locally with `make test` before pushing to `main`.

## Good

```bash
# Run the same suite CI runs, using .env.test, before pushing to main.
make test          # mvn -Dtest=com.ord.AllTestsSuite ... test
# Only push once it is green:
git push origin main
```

## Bad

```bash
# Pushing to main without running the suite — risks a failed Heroku deploy.
git push origin main   # CI runs AllTestsSuite for the first time and fails
```
