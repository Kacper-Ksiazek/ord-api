# Jira Target

> Single source of truth for this repository. MCP must use **only** this site and project for create/search/update unless user explicitly overrides in chat.

**Target (skrót):** `ORD - API` (`OA`)  
**Link:** https://kksiazek.atlassian.net/jira/software/projects/OA/boards/34

| Level | Name | ID / Key |
|-------|------|----------|
| Site | kksiazek.atlassian.net | 1a678711-0c91-477f-95d6-f54b7c3df444 |
| Project | ORD - API | OA (id: 10033) |

## MCP

Atlassian/Jira MCP is **not** committed in `.cursor/mcp.json` — enable the Atlassian plugin in Cursor (per-developer OAuth).

## Epics (by name — do not hardcode keys)

Issue keys (`OA-*`) change when epics are recreated. Resolve the current key at runtime, e.g.:

`project = OA AND issuetype = Epic AND summary = "QAW"`

| Epic (summary) | Zakres |
|----------------|--------|
| QAW | Quickly Added Words — CRUD, AI fill-gaps, public endpoint |
| CI/CD | GHCR publish, docker-compose.e2e, workflow guards |
| AI Rules | `docs/ai-rules/` knowledge base, agent conventions |
| Conversations | AI conversation practice, messages, analysis, tips |
| Games | Crossword, sentences writing, words typing variants |

## jql_scope (optional)

JQL fragment appended to all searches (without leading `AND`). Use `—` for whole project (skip the fragment entirely):

```
—
```

## Rules

- All issue **create** and **search** operations use `cloud_id` and `project_key` above.
- When `jql_scope` is `—`, do not append any scope fragment to JQL.
- When `jql_scope` is a non-empty JQL fragment, append it to searches.
- Do not browse other projects without explicit user instruction.
- Cross-repo work (ord-frontend) stays in `ORDUI`; link issues, do not duplicate.
- If site or project changes in Jira, update this file manually or via skill `jira-setup-project`.
