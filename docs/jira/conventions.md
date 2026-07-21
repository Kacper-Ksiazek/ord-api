# Jira Conventions

> **Cel pliku:** jak tworzyć i nazywać issue w projekcie `OA`.  
> **Gdzie:** site, project key, epiki, JQL scope → `docs/jira/target.md`.

Identyfikatorem issue jest **klucz Jiry** (`OA-42`) zwracany przez API — nie duplikuj go w polu `summary`.

---

## Hierarchia

```
Epic          → temat / inicjatywa (feature area)
  Task        → faza, feature chunk lub samodzielna jednostka pracy
    Subtask   → konkretny krok implementacyjny pod Taskiem
```

| Poziom | Kiedy | Issue type | Parent / link |
|--------|-------|------------|---------------|
| Epic | Grupowanie większej inicjatywy | Epic | — |
| Task | Faza, feature, bugfix bez rozbicia | Task | epic link (parent w next-gen) |
| Subtask | Atomowa praca pod istniejącym Taskiem | Subtask | `parent`: klucz Taska |

**Epic:** szukaj po `summary` (patrz `target.md`), nie hardcoduj `OA-*` w repo.

---

## Summary (tytuł)

Krótki, czytelny tytuł. **Bez prefiksów** ani tagów w summary — kontekst daje epic, projekt i klucz issue.

| Kontekst | Format | Przykład |
|----------|--------|----------|
| Faza w epicu | `Faza N — <cel>` | `Faza 1 — QAW fill-gaps retry` |
| Subtask | `<co> — <szczegół>` | `Test — stabilizacja batch fill-gaps` |
| Samodzielny Task | `<krótki tytuł>` | `GHCR publish workflow` |
| Bug | `<co nie działa>` | `Flaky test — TestQAWAIController.FillGaps` |

W raportach i PR-ach podawaj **klucz Jiry** (`OA-12`), nie prefiks w tytule.

---

## Issue types (OA)

| Sytuacja | Typ |
|----------|-----|
| Nowa praca (domyślnie) | Task |
| Błąd | Bug |
| Krok pod Taskiem | Subtask |
| Inicjatywa / grupa | Epic |

Nazwy typów są case-sensitive — zweryfikuj w projekcie przez Atlassian MCP (`getJiraProjectIssueTypesMetadata`).

---

## Statusy

| Intencja | Jira status | Uwagi |
|----------|-------------|-------|
| Nie zaczęte | To Do | |
| W toku / review | In Progress | |
| Ukończone | Done | resolution: Done |
| Anulowane | Done | resolution + komentarz `cancelled` |

Zmiana statusu: `getTransitionsForJiraIssue` → `transitionJiraIssue` (nigdy bezpośrednio pole `status`).

---

## Domyślne pola (nowe issue)

| Pole | Wartość |
|------|---------|
| Priority | Medium |
| Assignee | nie ustawiaj (chyba że user wskaże) |
| Labels / Components | nie ustawiaj (chyba że user wskaże) |

---

## Szablon opisu

```markdown
## Kontekst
<!-- dlaczego ten issue istnieje -->

## Kryteria akceptacji
- [ ] ...

## Linki
- Jira: OA-…
- Branch/PR: ...
- Docs: ...

## Definition of Done
- [ ] ...
```

---

## Reguły dla agenta

1. **Search-before-create** — szukaj duplikatu po `summary` w `project = OA` zanim utworzysz issue.
2. **Klucz z API** — po utworzeniu używaj `OA-N` w odpowiedziach i linkach.
3. **Epic** — przed linkowaniem znajdź epic JQL-em z `target.md`.
4. **Subtask** — wymaga `parent` (klucz Taska) i typu `Subtask`.
5. **Zapis do Jiry** — skill `jira-manage-tasks` (preview → OK użytkownika → MCP).
