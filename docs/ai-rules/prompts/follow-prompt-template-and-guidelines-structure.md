# Follow the shared prompt template structure and `guidelines.md`

Author every prompt `.md` following the conventions in `src/main/resources/prompts/guidelines.md`: a `### SYSTEM ROLE:` section, then task/context/instruction sections, with all dynamic values written as `{{camelCase}}` placeholders (the only accepted delimiter). When a prompt uses a structured-output schema, the schema enforces the response shape, so do not also embed TypeScript/JSON format blocks in the template.

## Good

```md
### SYSTEM ROLE:

You are a creative assistant helping to generate AI conversation partner identities for language learning.

### CONTEXT:

1. Language: {{language}} at the level of {{level}}.
2. Conversation topic: {{topic}}.
3. Available avatars with their genders: {{availableAvatars}}.

### TASK INSTRUCTIONS:

Generate a fitting AI interlocutor identity with a culturally appropriate name and avatarId.
```

## Bad

```md
You make a conversation partner.

Language: %%language%%        // wrong delimiter
Topic: ${topic}              // wrong delimiter

Return JSON like:
interface Response { name: string; avatarId: string }  // schema already enforces this
```
