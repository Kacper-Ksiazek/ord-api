# Stream example sentences after a blank line, each prefixed with `» `

When a streamed plain-text prompt should show example sentences as separate lines, do not bury them in the paragraph and do not use markdown bullets. The explanation stays one paragraph. Then exactly one blank line. Then one example per line, each starting with `» ` (guillemet, then one space).

The string SSE stream already preserves those newlines: the server writes a multiline chunk as several `data:` lines in one event, and the client joins them back with `\n`. The frontend can stream the paragraph immediately, then split off each complete `» ` line as its own sentence.

The example sentence itself is in the language being learned. Glosses and the explanation stay in the paragraph, in the generative content language. If there are no examples, omit the blank line and the example block.

`explain_word.md` still returns one paragraph. This layout applies to the AI explainer follow-up prompts.

## Good

```text
The word "hund" means "dog". It is a common everyday noun.

» Jeg har en hund.
» Hunden sover i sengen.
```

```md
### EXAMPLE LINES:

1. The explanation is a single paragraph. Do not put example sentences inside it
2. Exactly one blank line separates the paragraph from the examples
3. Each example is its own line and starts with "» " (guillemet, then one space)
4. Write examples in {{wordLanguage}}. Do not number them, bullet them, or wrap them in markdown
5. If you have none, omit the blank line and the example block
```

## Bad

```text
The word "hund" means "dog". For example, you might say "Jeg har en hund" when you own a dog. Another common use is "Hunden sover".
```

```text
The word "hund" means "dog".

- Jeg har en hund.
1. Hunden sover i sengen.
```
