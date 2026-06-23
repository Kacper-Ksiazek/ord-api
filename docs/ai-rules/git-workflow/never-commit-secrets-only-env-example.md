# Never commit secrets — only `.env.example`

Real secrets (`.env`, `.env.test`, API keys, JWT secret, SMTP credentials, OpenAI key) must never be committed. `.gitignore` already excludes `.env` and `.env.*`; keep it that way and only commit the placeholder `.env.example` with empty values. Never add real values to tracked files.

## Good

```bash
# .env.example is tracked and holds only empty placeholders:
#   JWT_SECRET_KEY=
#   OPEN_AI_KEY=
#   SMTP_PASSWORD=
git add .env.example
# Real values live only in the gitignored .env / .env.test
```

## Bad

```bash
# Force-adding a secret-bearing file past .gitignore.
git add -f .env
git commit -m "add config"   # leaks JWT_SECRET_KEY, OPEN_AI_KEY, SMTP_PASSWORD
```
