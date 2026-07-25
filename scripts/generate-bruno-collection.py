#!/usr/bin/env python3
"""Generate Bruno HTTP collection for ORD API."""

from __future__ import annotations

import json
import textwrap
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
OUT = ROOT / "bruno" / "ord-api"


def folder_meta(name: str, seq: int) -> str:
    return f"""meta {{
  name: {name}
  seq: {seq}
}}
"""


def http_request(
    *,
    name: str,
    seq: int,
    method: str,
    url: str,
    auth: str = "inherit",
    body: str | None = None,
    headers: dict[str, str] | None = None,
    query: dict[str, str] | None = None,
    post_response: str | None = None,
    docs: str | None = None,
) -> str:
    lines = [
        "meta {",
        f"  name: {name}",
        "  type: http",
        f"  seq: {seq}",
        "}",
        "",
    ]
    if docs:
        lines.extend(["docs {", f"  {docs}", "}", ""])

    block = method.lower()
    lines.append(f"{block} {{")
    lines.append(f"  url: {url}")
    if body is not None:
        lines.append("  body: json")
    else:
        lines.append("  body: none")
    lines.append(f"  auth: {auth}")
    lines.append("}")

    if headers or query:
        lines.append("")
        if query:
            lines.append("params:query {")
            for k, v in query.items():
                lines.append(f"  {k}: {v}")
            lines.append("}")
        if headers:
            lines.append("")
            lines.append("headers {")
            for k, v in headers.items():
                lines.append(f"  {k}: {v}")
            lines.append("}")

    if body is not None:
        lines.append("")
        lines.append("body:json {")
        lines.append(body)
        lines.append("}")

    if post_response:
        lines.append("")
        lines.append("script:post-response {")
        lines.extend(f"  {line}" for line in post_response.strip().splitlines())
        lines.append("}")

    return "\n".join(lines) + "\n"


BASE = "{{baseUrl}}{{apiPrefix}}"

ENDPOINTS: list[tuple[str, str, dict]] = [
    # (folder_path, filename, request_kwargs)
    ("00-setup", "health-check", dict(
        name="Health Check", seq=1, method="GET", url=f"{BASE}/health-check", auth="none",
    )),
    ("01-auth", "otp-request", dict(
        name="OTP Request", seq=1, method="POST", url=f"{BASE}/auth/otp-request", auth="none",
        headers={"Content-Type": "application/json"},
        body='{\n  "email": "{{email}}"\n}',
        docs="Anonymous-only: must not send AUTH-TOKEN cookie.",
    )),
    ("01-auth", "otp-verify", dict(
        name="OTP Verify", seq=2, method="POST", url=f"{BASE}/auth/otp-verify", auth="none",
        headers={"Content-Type": "application/json"},
        body='{\n  "email": "{{email}}",\n  "code": "{{otpCode}}"\n}',
        post_response=textwrap.dedent("""
            const body = res.getBody();
            if (body?.id) {
              bru.setEnvVar("userId", body.id);
            }
        """),
        docs="Sets AUTH-TOKEN cookie in Bruno cookie jar.",
    )),
    ("01-auth", "logout", dict(
        name="Logout", seq=3, method="DELETE", url=f"{BASE}/auth/logout", auth="inherit",
    )),
    ("02-users", "get-me", dict(
        name="Get Me", seq=1, method="GET", url=f"{BASE}/users/me",
    )),
    ("02-users", "init-account", dict(
        name="Init Account", seq=2, method="POST", url=f"{BASE}/users/init-account",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "name": "Dev User",
              "nativeLanguage": "POLISH",
              "selectedLearningLanguage": "ENGLISH",
              "languageProficiencies": [
                {
                  "language": "ENGLISH",
                  "level": "B2",
                  "translateTo": "POLISH",
                  "generativeContentLanguage": "ENGLISH"
                }
              ]
            }
        """).strip(),
    )),
    ("03-language-proficiencies", "list", dict(
        name="List Language Proficiencies", seq=1, method="GET", url=f"{BASE}/language-proficiencies",
    )),
    ("03-language-proficiencies", "create", dict(
        name="Create Language Proficiency", seq=2, method="POST", url=f"{BASE}/language-proficiencies",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "language": "GERMAN",
              "level": "B1",
              "translateTo": "ENGLISH",
              "generativeContentLanguage": "GERMAN"
            }
        """).strip(),
    )),
    ("03-language-proficiencies", "update", dict(
        name="Update Language Proficiency", seq=3, method="PATCH", url=f"{BASE}/language-proficiencies",
        headers={"Content-Type": "application/json"},
        body='{\n  "language": "GERMAN",\n  "level": "B2"\n}',
    )),
    ("03-language-proficiencies", "delete-by-language", dict(
        name="Delete Language Proficiency", seq=4, method="DELETE",
        url=f"{BASE}/language-proficiencies/{{{{language}}}}",
    )),
    ("04-words/crud", "get-many-words", dict(
        name="Get Many Words", seq=1, method="POST", url=f"{BASE}/words/get-many-words",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "language": "ENGLISH",
              "page": 0,
              "perPage": 20,
              "searchingPhrase": null,
              "wordType": null,
              "wordExtraMark": null,
              "bookmarked": null,
              "completed": null,
              "banksIds": null,
              "bankGroupsIds": null,
              "sortDirection": "DESC",
              "sortBy": "CREATED_AT"
            }
        """).strip(),
        post_response=textwrap.dedent("""
            const body = res.getBody();
            const first = body?.data?.[0];
            if (first?.bankId) {
              bru.setEnvVar("bankId", first.bankId);
            }
        """),
    )),
    ("04-words/crud", "get-by-id", dict(
        name="Get Word By ID", seq=2, method="GET", url=f"{BASE}/words/{{{{wordId}}}}",
    )),
    ("04-words/crud", "create", dict(
        name="Create Word", seq=3, method="POST", url=f"{BASE}/words/",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "type": "NOUN",
              "sourceWord": "ephemeral",
              "translation": "przemijajacy",
              "definition": "Lasting for a very short time",
              "language": "ENGLISH",
              "extraMark": null,
              "bankId": null,
              "bankToCreate": null
            }
        """).strip(),
        post_response=textwrap.dedent("""
            const body = res.getBody();
            if (body?.id) {
              bru.setEnvVar("wordId", body.id);
            }
            if (body?.bankId) {
              bru.setEnvVar("bankId", body.bankId);
            }
        """),
    )),
    ("04-words/crud", "update", dict(
        name="Update Word", seq=4, method="PATCH", url=f"{BASE}/words/{{{{wordId}}}}",
        headers={"Content-Type": "application/json"},
        body='{\n  "translation": "przemijajacy (updated)"\n}',
    )),
    ("04-words/crud", "delete", dict(
        name="Delete Word", seq=5, method="DELETE", url=f"{BASE}/words/{{{{wordId}}}}",
    )),
    ("04-words/crud", "change-bank", dict(
        name="Change Word Bank", seq=6, method="POST", url=f"{BASE}/words/{{{{wordId}}}}/change-bank",
        headers={"Content-Type": "application/json"},
        body='{\n  "bankId": "{{bankId}}"\n}',
    )),
    ("04-words/crud", "change-bank-bulk", dict(
        name="Change Bank For Multiple Words", seq=7, method="POST",
        url=f"{BASE}/words/change-bank-for-multiple-words",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "bankId": "{{bankId}}",
              "wordIds": ["{{wordId}}"]
            }
        """).strip(),
    )),
    ("04-words/crud", "toggle-property", dict(
        name="Toggle Word Property", seq=8, method="POST",
        url=f"{BASE}/words/{{{{wordId}}}}/toggle-property",
        query={"property": "FAVORITE"},
    )),
    ("04-words/crud", "toggle-property-bulk", dict(
        name="Toggle Property For Multiple Words", seq=9, method="POST",
        url=f"{BASE}/words/toggle-property-for-multiple-words",
        query={"property": "FAVORITE"},
        headers={"Content-Type": "application/json"},
        body='{\n  "wordIds": ["{{wordId}}"]\n}',
    )),
    ("04-words/details", "create-details", dict(
        name="Create Word Details", seq=1, method="POST", url=f"{BASE}/words/{{{{wordId}}}}/details",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "useCases": ["greeting", "informal"],
              "synonyms": ["hi", "hey"],
              "antonyms": ["goodbye"],
              "commonMistakes": ["Avoid in formal writing"],
              "exampleSentences": [
                {
                  "context": "Meeting a friend",
                  "sentence": "Hello! How are you?",
                  "translation": "Czesc! Jak sie masz?"
                }
              ],
              "collocations": [
                {
                  "phrase": "say hello",
                  "translation": "przywitac sie",
                  "frequency": "VERY_COMMON"
                }
              ],
              "pronunciation": null,
              "grammar": null,
              "culturalNotes": "Common greeting",
              "learningTips": "Stress on the second syllable"
            }
        """).strip(),
    )),
    ("04-words/details", "update-details", dict(
        name="Update Word Details", seq=2, method="PATCH", url=f"{BASE}/words/{{{{wordId}}}}/details",
        headers={"Content-Type": "application/json"},
        body='{\n  "learningTips": "Updated learning tips."\n}',
    )),
    ("04-words/ai", "generate-manual", dict(
        name="Generate AI Word Manual", seq=1, method="POST", url=f"{BASE}/words/ai/generate-manual",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "word": "ephemeral",
              "language": "ENGLISH",
              "targetLanguage": "POLISH",
              "proficiencyLevel": "B2"
            }
        """).strip(),
        docs="Requires OPEN_AI_KEY on the API server.",
    )),
    ("04-words/ai", "suggest-vocabulary-sse", dict(
        name="Suggest Vocabulary (SSE)", seq=2, method="POST", url=f"{BASE}/words/ai/suggest-vocabulary",
        headers={"Content-Type": "application/json", "Accept": "text/event-stream"},
        body=textwrap.dedent("""
            {
              "language": "ENGLISH",
              "context": "business meetings"
            }
        """).strip(),
        docs="SSE stream. Enable streaming in Bruno. Requires OPEN_AI_KEY.",
    )),
    ("05-quickly-added-words", "create", dict(
        name="Create QAW", seq=1, method="POST", url=f"{BASE}/quickly-added-words/",
        headers={"Content-Type": "application/json"},
        body='{\n  "word": "serendipity",\n  "language": "ENGLISH"\n}',
        post_response=textwrap.dedent("""
            const body = res.getBody();
            if (body?.id) {
              bru.setEnvVar("qawId", body.id);
            }
        """),
    )),
    ("05-quickly-added-words", "bulk-create", dict(
        name="Bulk Create QAW", seq=2, method="POST", url=f"{BASE}/quickly-added-words/bulk-create",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            [
              { "word": "ephemeral", "language": "ENGLISH" },
              { "word": "ubiquitous", "language": "ENGLISH" }
            ]
        """).strip(),
    )),
    ("05-quickly-added-words", "list", dict(
        name="List QAW", seq=3, method="GET", url=f"{BASE}/quickly-added-words/",
        query={"page": "0", "perPage": "20", "isApproved": "false"},
    )),
    ("05-quickly-added-words", "overview", dict(
        name="QAW Overview", seq=4, method="GET", url=f"{BASE}/quickly-added-words/overview",
    )),
    ("05-quickly-added-words", "update", dict(
        name="Update QAW", seq=5, method="PATCH", url=f"{BASE}/quickly-added-words/{{{{qawId}}}}",
        headers={"Content-Type": "application/json"},
        body='{\n  "word": "serendipity (edited)"\n}',
    )),
    ("05-quickly-added-words", "bulk-update", dict(
        name="Bulk Update QAW", seq=6, method="PATCH", url=f"{BASE}/quickly-added-words/bulk-update",
        headers={"Content-Type": "application/json"},
        body='{\n  "{{qawId}}": "serendipity-updated"\n}',
    )),
    ("05-quickly-added-words", "approve-many", dict(
        name="Approve Many QAW", seq=7, method="PATCH", url=f"{BASE}/quickly-added-words/approve-many",
        headers={"Content-Type": "application/json"},
        body='{\n  "ids": ["{{qawId}}"]\n}',
    )),
    ("05-quickly-added-words", "delete", dict(
        name="Delete QAW", seq=8, method="DELETE", url=f"{BASE}/quickly-added-words/{{{{qawId}}}}",
    )),
    ("05-quickly-added-words", "bulk-delete", dict(
        name="Bulk Delete QAW", seq=9, method="POST", url=f"{BASE}/quickly-added-words/bulk-delete",
        headers={"Content-Type": "application/json"},
        body='{\n  "ids": ["{{qawId}}"]\n}',
    )),
    ("05-quickly-added-words/ai", "fill-gaps", dict(
        name="QAW AI Fill Gaps", seq=1, method="POST", url=f"{BASE}/quickly-added-words/ai/fill-gaps",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "language": "ENGLISH",
              "items": [
                { "word": "ephemeral" },
                { "word": "ubiquitous" }
              ]
            }
        """).strip(),
        docs="Requires OPEN_AI_KEY. Does not persist results.",
    )),
    ("06-public-qaw", "bulk-create", dict(
        name="Public Bulk Create QAW", seq=1, method="POST",
        url=f"{BASE}/public/quickly-added-words/bulk-create", auth="none",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "userEmail": "{{email}}",
              "language": "ENGLISH",
              "words": [
                { "word": "serendipity" }
              ]
            }
        """).strip(),
        docs="Anonymous-only: must not send AUTH-TOKEN cookie.",
    )),
    ("07-games", "cancel-game", dict(
        name="Cancel Game", seq=1, method="POST", url=f"{BASE}/games/cancel/{{{{gameId}}}}",
        headers={"Content-Type": "application/json"},
        body='{\n  "duration": "PT2M30S"\n}',
    )),
    ("07-games/crossword", "start", dict(
        name="Start Crossword", seq=1, method="POST", url=f"{BASE}/games/crossword/start",
        headers={"Content-Type": "application/json"},
        body='{\n  "difficulty": "MEDIUM",\n  "language": "ENGLISH"\n}',
        post_response=textwrap.dedent("""
            const body = res.getBody();
            if (body?.gameId) {
              bru.setEnvVar("gameId", body.gameId);
            }
        """),
    )),
    ("07-games/crossword", "finish", dict(
        name="Finish Crossword", seq=2, method="POST", url=f"{BASE}/games/crossword/finish",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "gameId": "{{gameId}}",
              "duration": "PT5M30S",
              "answers": []
            }
        """).strip(),
        docs="Paste full answers from start response. See _fixtures/bodies/crossword-finish.json.",
    )),
    ("07-games/words-typing", "start", dict(
        name="Start Words Typing", seq=1, method="POST", url=f"{BASE}/games/words-typing/start",
        headers={"Content-Type": "application/json"},
        body='{\n  "difficulty": "MEDIUM",\n  "language": "ENGLISH"\n}',
        post_response=textwrap.dedent("""
            const body = res.getBody();
            if (body?.gameId) {
              bru.setEnvVar("gameId", body.gameId);
            }
        """),
    )),
    ("07-games/words-typing", "finish", dict(
        name="Finish Words Typing", seq=2, method="POST", url=f"{BASE}/games/words-typing/finish",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "gameId": "{{gameId}}",
              "duration": "PT3M",
              "answers": [
                {
                  "questionId": "00000000-0000-0000-0000-000000000001",
                  "answer": "example"
                }
              ]
            }
        """).strip(),
        docs="Replace questionId/answers from start response.",
    )),
    ("07-games/sentences-writing", "start", dict(
        name="Start Sentences Writing", seq=1, method="POST", url=f"{BASE}/games/sentences-writing/start",
        headers={"Content-Type": "application/json"},
        body='{\n  "difficulty": "MEDIUM",\n  "language": "ENGLISH"\n}',
        post_response=textwrap.dedent("""
            const body = res.getBody();
            if (body?.gameId) {
              bru.setEnvVar("gameId", body.gameId);
            }
        """),
    )),
    ("07-games/sentences-writing", "finish", dict(
        name="Finish Sentences Writing", seq=2, method="POST", url=f"{BASE}/games/sentences-writing/finish",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "gameId": "{{gameId}}",
              "duration": "PT10M",
              "answers": {
                "00000000-0000-0000-0000-000000000001": "I enjoyed the transient nature of the trip."
              }
            }
        """).strip(),
        docs="Replace keys from start response. Max 1024 chars per answer.",
    )),
    ("08-conversations", "suggest-topics-sse", dict(
        name="Suggest Topics (SSE)", seq=1, method="POST", url=f"{BASE}/conversations/suggest-topics",
        headers={"Content-Type": "application/json", "Accept": "text/event-stream"},
        body=textwrap.dedent("""
            {
              "clueFromUser": "travel and holidays",
              "conversationType": "SMALL_TALK",
              "language": "ENGLISH",
              "excludeTopics": []
            }
        """).strip(),
        docs="SSE stream. Requires OPEN_AI_KEY.",
    )),
    ("08-conversations", "suggest-ai-interlocutor", dict(
        name="Suggest AI Interlocutor", seq=2, method="POST",
        url=f"{BASE}/conversations/suggest-ai-interlocutor",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "topic": "Discussing the weather and climate change",
              "additionalContext": "Focus on environmental impact",
              "conversationType": "SMALL_TALK",
              "language": "ENGLISH",
              "recentInterlocutors": []
            }
        """).strip(),
    )),
    ("08-conversations", "list", dict(
        name="List Conversations", seq=3, method="GET", url=f"{BASE}/conversations/",
        query={"search": "", "recencyBucket": "", "type": ""},
    )),
    ("08-conversations", "overview", dict(
        name="Conversation Activity Overview", seq=4, method="GET", url=f"{BASE}/conversations/overview",
    )),
    ("08-conversations", "get-by-id", dict(
        name="Get Conversation By ID", seq=5, method="GET", url=f"{BASE}/conversations/{{{{conversationId}}}}",
    )),
    ("08-conversations", "create", dict(
        name="Create Conversation", seq=6, method="POST", url=f"{BASE}/conversations/",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "topic": "Discussing the weather and climate change",
              "additionalContext": "Focus on environmental impact",
              "language": "ENGLISH",
              "tone": "FRIENDLY",
              "type": "SMALL_TALK",
              "aiInterlocutorName": "Dr. Smith",
              "aiInterlocutorAvatarId": "AVATAR_ALPHA"
            }
        """).strip(),
        post_response=textwrap.dedent("""
            const body = res.getBody();
            if (body?.id) {
              bru.setEnvVar("conversationId", body.id);
            }
        """),
    )),
    ("08-conversations", "delete", dict(
        name="Delete Conversation", seq=7, method="DELETE",
        url=f"{BASE}/conversations/{{{{conversationId}}}}",
    )),
    ("08-conversations/ongoing", "ai-initialize-sse", dict(
        name="AI Initialize Conversation (SSE)", seq=1, method="POST",
        url=f"{BASE}/conversations/ongoing/ai/initialize",
        query={"conversationId": "{{conversationId}}"},
        headers={"Accept": "text/event-stream"},
        docs="SSE stream. Requires conversationId from create.",
    )),
    ("08-conversations/ongoing", "ai-request-message-sse", dict(
        name="AI Request Message (SSE)", seq=2, method="POST",
        url=f"{BASE}/conversations/ongoing/ai/request-message",
        headers={"Content-Type": "application/json", "Accept": "text/event-stream"},
        body=textwrap.dedent("""
            {
              "conversationId": "{{conversationId}}",
              "messageOrder": 2,
              "latestUserMessage": "I think climate change is a serious issue."
            }
        """).strip(),
        docs="SSE stream. Set messageOrder from conversation state.",
    )),
    ("08-conversations/ongoing", "ai-generate-learning-tips", dict(
        name="AI Generate Learning Tips", seq=3, method="POST",
        url=f"{BASE}/conversations/ongoing/ai/generate-learning-tips",
        headers={"Content-Type": "application/json"},
        body='{\n  "conversationId": "{{conversationId}}"\n}',
    )),
    ("08-conversations/ongoing", "user-save-message", dict(
        name="Save User Message", seq=4, method="POST",
        url=f"{BASE}/conversations/ongoing/user/save-message",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "conversationId": "{{conversationId}}",
              "messageId": "00000000-0000-0000-0000-000000000001",
              "content": "Hello! I think climate change is a serious issue.",
              "messageOrder": 1
            }
        """).strip(),
    )),
    ("08-conversations/ongoing", "user-generate-analysis", dict(
        name="Generate User Message Analysis", seq=5, method="POST",
        url=f"{BASE}/conversations/ongoing/user/generate-analysis",
        headers={"Content-Type": "application/json"},
        body=textwrap.dedent("""
            {
              "conversationId": "{{conversationId}}",
              "messageId": "00000000-0000-0000-0000-000000000001",
              "messageOrder": 1,
              "latestAIMessage": null
            }
        """).strip(),
    )),
    ("09-ai-explainer", "explain-phrase-sse", dict(
        name="Explain Phrase (SSE)", seq=1, method="POST", url=f"{BASE}/ai-explainer/explain-phrase",
        headers={"Content-Type": "application/json", "Accept": "text/event-stream"},
        body=textwrap.dedent("""
            {
              "phrase": "ephemeral",
              "language": "ENGLISH",
              "context": "The beauty of cherry blossoms is ephemeral.",
              "customInstruction": "Focus on everyday usage"
            }
        """).strip(),
        docs="SSE stream. Requires OPEN_AI_KEY.",
    )),
]

FOLDER_SEQ = {
    "00-setup": 1,
    "01-auth": 2,
    "02-users": 3,
    "03-language-proficiencies": 4,
    "04-words": 5,
    "04-words/crud": 6,
    "04-words/details": 7,
    "04-words/ai": 8,
    "05-quickly-added-words": 9,
    "05-quickly-added-words/ai": 10,
    "06-public-qaw": 11,
    "07-games": 12,
    "07-games/crossword": 13,
    "07-games/words-typing": 14,
    "07-games/sentences-writing": 15,
    "08-conversations": 16,
    "08-conversations/ongoing": 17,
    "09-ai-explainer": 18,
    "99-workflows": 19,
    "_fixtures": 20,
}


def write_environments() -> None:
    env_dir = OUT / "environments"
    env_dir.mkdir(parents=True, exist_ok=True)
    (env_dir / "local.bru").write_text(
        """vars {
  baseUrl: http://localhost:8080
  apiPrefix: /api/v1
  email: dev@example.com
  otpCode: 123456
  authCookieName: AUTH-TOKEN
  swaggerUser: admin
  swaggerPassword: admin
  userId:
  wordId:
  conversationId:
  gameId:
  bankId:
  qawId:
  language: ENGLISH
}
""",
        encoding="utf-8",
    )
    (env_dir / "production.bru").write_text(
        """vars {
  baseUrl: https://api.ord-platform.com
  apiPrefix: /api/v1
  email:
  otpCode:
  authCookieName: AUTH-TOKEN
  userId:
  wordId:
  conversationId:
  gameId:
  bankId:
  qawId:
  language: ENGLISH
}
""",
        encoding="utf-8",
    )


def write_fixtures() -> None:
    bodies = OUT / "_fixtures" / "bodies"
    bodies.mkdir(parents=True, exist_ok=True)

    fixtures = {
        "create-word.json": {
            "type": "NOUN",
            "sourceWord": "ephemeral",
            "translation": "przemijajacy",
            "definition": "Lasting for a very short time",
            "language": "ENGLISH",
        },
        "init-account.json": {
            "name": "Dev User",
            "nativeLanguage": "POLISH",
            "selectedLearningLanguage": "ENGLISH",
            "languageProficiencies": [
                {
                    "language": "ENGLISH",
                    "level": "B2",
                    "translateTo": "POLISH",
                    "generativeContentLanguage": "ENGLISH",
                }
            ],
        },
        "start-game.json": {"difficulty": "MEDIUM", "language": "ENGLISH"},
        "create-conversation.json": {
            "topic": "Discussing the weather and climate change",
            "additionalContext": "Focus on environmental impact",
            "language": "ENGLISH",
            "tone": "FRIENDLY",
            "type": "SMALL_TALK",
            "aiInterlocutorName": "Dr. Smith",
            "aiInterlocutorAvatarId": "AVATAR_ALPHA",
        },
        "crossword-finish.json": {
            "gameId": "{{gameId}}",
            "duration": "PT5M30S",
            "answers": [],
        },
    }
    for name, data in fixtures.items():
        (bodies / name).write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")

    (OUT / "_fixtures" / "enums.md").write_text(
        """# ORD API enums (reference for Bruno requests)

## LanguageName
POLISH, ENGLISH, GERMAN, FRENCH, SPANISH, ITALIAN, NORWEGIAN, RUSSIAN, SLOVENIAN

## LanguageProficiencyLevel
A1, A2, B1, B2, C1, C2

## WordType
NOUN, VERB, ADJECTIVE, ADVERB, PHRASE, OTHER

## WordToggleableProperty (query param)
FAVORITE, COMPLETED

## GameDifficulty
EASY, MEDIUM, HARD

## ConversationType
SMALL_TALK, FREE_FORM, ROLE_PLAY, DEBATE

## ConversationTone
FORMAL, CASUAL, FRIENDLY, NEUTRAL

## GetAllWordsSortOptions
CREATED_AT, UPDATED_AT, SOURCE_WORD

## SortDirection
ASC, DESC
""",
        encoding="utf-8",
    )


def write_workflows_readme() -> None:
    workflow_dir = OUT / "99-workflows"
    workflow_dir.mkdir(parents=True, exist_ok=True)
    (workflow_dir / "folder.bru").write_text(folder_meta("99 Workflows", 19), encoding="utf-8")

    workflows = [
        ("01-new-user-onboarding", "New User Onboarding", """
Run in order:
1. 00-setup/health-check
2. 01-auth/otp-request -> otp-verify
3. 02-users/get-me (isAccountInitialized: false)
4. 02-users/init-account
5. 02-users/get-me (isAccountInitialized: true)
6. 03-language-proficiencies/list
"""),
        ("02-word-lifecycle", "Word Lifecycle", """
Run in order:
1. Auth if needed
2. 04-words/crud/create -> wordId
3. 04-words/crud/get-by-id
4. 04-words/details/create-details
5. 04-words/ai/generate-manual (optional, needs OPEN_AI_KEY)
6. 04-words/crud/toggle-property?property=FAVORITE
7. 04-words/crud/get-many-words (bookmarked: true)
8. 04-words/crud/delete
"""),
        ("03-qaw-approve-flow", "QAW Approve Flow", """
Run in order:
1. 06-public-qaw/bulk-create (no cookie) OR 05-quickly-added-words/create
2. 05-quickly-added-words/list?isApproved=false
3. 05-quickly-added-words/ai/fill-gaps (optional)
4. 05-quickly-added-words/approve-many
5. 05-quickly-added-words/overview
"""),
        ("04-crossword-session", "Crossword Session", """
Run in order:
1. Auth + ensure user has words (get-many-words)
2. 07-games/crossword/start -> gameId + full game payload
3. 07-games/crossword/finish (paste live start response into body)
4. Optional: 07-games/cancel-game
"""),
        ("05-conversation-session", "Conversation Session", """
Run in order:
1. 08-conversations/suggest-ai-interlocutor (optional)
2. 08-conversations/create -> conversationId
3. 08-conversations/ongoing/ai-initialize-sse (SSE)
4. 08-conversations/ongoing/user-save-message
5. 08-conversations/ongoing/ai-request-message-sse (SSE)
6. 08-conversations/ongoing/user-generate-analysis
7. 08-conversations/ongoing/ai-generate-learning-tips
"""),
    ]

    for idx, (slug, title, body) in enumerate(workflows, start=1):
        content = f"""meta {{
  name: {title}
  type: http
  seq: {idx}
}}

docs {{
{body.strip()}
}}
"""
        (workflow_dir / f"{slug}.bru").write_text(content, encoding="utf-8")


def main() -> None:
    if OUT.exists():
        import shutil
        shutil.rmtree(OUT)

    OUT.mkdir(parents=True)
    (OUT / "bruno.json").write_text(
        json.dumps({"version": "1", "name": "ORD API", "type": "collection", "ignore": ["node_modules", ".git"]}, indent=2) + "\n",
        encoding="utf-8",
    )

    write_environments()
    write_fixtures()
    write_workflows_readme()

    created_folders: set[str] = set()
    for folder, filename, kwargs in ENDPOINTS:
        folder_path = OUT / folder
        folder_path.mkdir(parents=True, exist_ok=True)
        if folder not in created_folders:
            folder_name = folder.split("/")[-1].replace("-", " ").title()
            if folder.startswith("0"):
                folder_name = folder.replace("/", " - ")
            seq = FOLDER_SEQ.get(folder, 99)
            (folder_path / "folder.bru").write_text(folder_meta(folder_name, seq), encoding="utf-8")
            created_folders.add(folder)

        (folder_path / f"{filename}.bru").write_text(http_request(**kwargs), encoding="utf-8")

    readme = """# ORD API — Bruno Collection

Manual testing and debugging collection for all `/api/v1` endpoints.

## Requirements

- [Bruno](https://www.usebruno.com/) desktop app
- Running ORD API (`DATABASE_URL`, `JWT_SECRET_KEY`, `ENV_TEST_PROPERTY=1test1`)
- For local OTP: `OTP_WHITELISTED_EMAILS=dev@example.com` and code `123456`

## Quick start

1. Open this folder in Bruno (`bruno/ord-api`)
2. Select **local** environment
3. Run `00-setup/health-check`
4. Run `01-auth/otp-request` then `01-auth/otp-verify`
5. Run `02-users/get-me` (cookie `AUTH-TOKEN` is sent automatically)

## Authentication

JWT is stored in the **AUTH-TOKEN** HttpOnly cookie (not `Authorization: Bearer`).
After `otp-verify`, Bruno stores the cookie in its cookie jar for `localhost:8080`.

**Anonymous-only endpoints** (must NOT send AUTH-TOKEN):
- `POST /auth/otp-request`, `POST /auth/otp-verify`
- `GET /health-check`
- `POST /public/quickly-added-words/bulk-create`

Clear cookies or run `logout` before re-authenticating via OTP.

## SSE endpoints

These return `text/event-stream`. Enable response streaming in Bruno and set `Accept: text/event-stream`:

- `04-words/ai/suggest-vocabulary-sse`
- `08-conversations/suggest-topics-sse`
- `08-conversations/ongoing/ai-initialize-sse`
- `08-conversations/ongoing/ai-request-message-sse`
- `09-ai-explainer/explain-phrase-sse`

Requires `OPEN_AI_KEY` on the server. AI calls may take a long time.

Fallback: `curl -N -b cookies.txt -H "Accept: text/event-stream" ...`

## Workflows

See `99-workflows/` for step-by-step debug sequences.

## Variables (chained via post-response scripts)

| Variable | Set by |
|----------|--------|
| userId | otp-verify |
| wordId, bankId | create word, get-many-words |
| qawId | create QAW |
| conversationId | create conversation |
| gameId | game start endpoints |

## Sync policy

When API changes: update Kotlin/OpenAPI, run `make openapi`, then update matching `.bru` files.

## Swagger UI

`http://localhost:8080/swagger-ui.html` (Basic Auth: admin/admin)
"""
    (OUT / "README.md").write_text(readme, encoding="utf-8")

    print(f"Generated {len(ENDPOINTS)} requests in {OUT}")


if __name__ == "__main__":
    main()
