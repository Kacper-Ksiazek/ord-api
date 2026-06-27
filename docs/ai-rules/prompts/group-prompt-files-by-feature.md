# Group prompt files by feature, mirroring the code

Prompt templates are organised into per-feature subfolders under `src/main/resources/prompts/` (`conversation/`, `games/`, `words/`, `qaw/`). The `resourcePath` in `AvailablePrompts` must match this layout, and the structured-output schema for a prompt lives under the same feature folder in `structured_outputs/features/<feature>/`. Use snake_case, descriptive file names.

## Good

```text
src/main/resources/prompts/
├── conversation/
│   ├── generate_ai_interlocutor.md
│   └── respond_in_conversation.md
├── games/
│   └── generate_crossword_game.md
├── words/
│   └── generate_word_manual.md
├── qaw/
│   └── fill_gaps.md
└── guidelines.md

src/main/kotlin/com/ord/shared/prompts/structured_outputs/features/
├── conversation/GeneratedAIInterlocutorSchema.kt
├── games/CrosswordGenerateSchema.kt
└── words/GeneratedWordManualSchema.kt
```

## Bad

```text
# Flat, ungrouped, and inconsistent with the code's feature packages.
src/main/resources/prompts/
├── generateAiInterlocutor.md
├── crossword.md
└── fillGapsPrompt.md
```
