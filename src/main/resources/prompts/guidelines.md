# Prompt Engineering Guidelines

This document describes the standard pattern and best practices for creating AI prompt files in this application.

## File Structure

All prompt files should follow this consistent structure:

### 1. SYSTEM ROLE (Required)
Define who the AI is in the context of this specific task.

```markdown
### SYSTEM ROLE:

You are [specific role description].
```

**Examples:**
- `You are an expert foreign language tutor specializing in vocabulary building and personalized learning.`
- `You are a writing tutor in a vocabulary learning app.`
- `You are a foreign language private tutor.`

**Best Practices:**
- Be specific about the expertise domain
- Align the role with the task's purpose
- Keep it concise (1-2 sentences max)

---

### 2. TASK (Required)
Clearly state what the AI needs to accomplish.

```markdown
### TASK:

[Clear, action-oriented description of what needs to be done]
```

**Examples:**
- `Generate a personalized list of vocabulary suggestions for a language learner.`
- `Generate a detailed manual entry for the following word: "**%%word%%**"`
- `START a conversation with the user (your student) in the language of their choice.`

**Best Practices:**
- Use action verbs (Generate, Create, Analyze, Review, etc.)
- Be specific about the deliverable
- Include critical context inline if necessary (e.g., emphasize specific words with bold)

---

### 3. CONTEXT (Required)
Provide all necessary contextual information using template variables.

```markdown
### CONTEXT:

1. [Context Item 1]: **%%variable1%%**
2. [Context Item 2]: **%%variable2%%**
3. [Context Item 3]: **%%variable3%%**
```

**Template Variables:**
- Use the pattern `%%variableName%%` for all dynamic values
- Wrap variables in bold markdown (`**%%variableName%%**`) for emphasis
- Number context items for clarity
- Include both required and optional context

**Common Context Items:**
- Language information (target language, translation language, proficiency level)
- User-specific data (user context, preferences)
- Configuration (difficulty, word count, generative content language)

**Best Practices:**
- Label optional context items explicitly: `(optional)`
- Provide clear, descriptive labels for each context item
- Order from most to least important

---

### 4. GUIDELINES (Required)
Detailed instructions on how to approach the task.

```markdown
### GUIDELINES:

- [Guideline 1]
  - [Sub-guideline 1a]
  - [Sub-guideline 1b]
- [Guideline 2]
- [Guideline 3]
```

**What to Include:**
- **Scope constraints**: What to include and exclude
- **Quality criteria**: Standards the output must meet
- **Proficiency-based adjustments**: Different approaches for different skill levels
- **Priority rules**: What to prioritize or deprioritize
- **Edge case handling**: Special considerations

**Best Practices:**
- Use nested bullet points for sub-guidelines
- Be specific about proficiency level adjustments (A1-A2, B1-B2, C1-C2)
- Include both positive guidance ("Do this") and negative guidance ("Avoid this")
- Provide concrete examples where helpful

**Common Guideline Categories:**
- Proficiency-based content adjustment
- Content filtering (what to avoid)
- Quality criteria (what to prioritize)
- Contextual relevance
- Diversity and variety requirements

---

### 5. RESPONSE FORMAT (Required for Structured Output)
Define the exact structure of the expected response using TypeScript interfaces.

```markdown
### RESPONSE FORMAT:

Return [description] matching this TypeScript interface:

\`\`\`ts
interface Response {
    /** Field description */
    fieldName: string

    /** Optional field */
    optionalField: string | null

    /** Array field */
    items: ItemType[]
}
\`\`\`
```

**Best Practices:**
- Always use TypeScript interface syntax
- Include JSDoc comments for each field
- Specify nullable fields with `| null`
- Use descriptive interface names
- Include nested interfaces when needed
- Reference template variables in field descriptions (e.g., `Translation into **%%desiredLanguage%%**`)
- Define enum values explicitly when applicable (e.g., `Values: **%%wordTypes%%**`)

---

### 6. OUTPUT STRUCTURE (Required for Complex Outputs)
Provide concrete examples of how the output should be formatted.

```markdown
### OUTPUT STRUCTURE:

Format output exactly as follows:
\`\`\`
[Example output with placeholders]
\`\`\`
```

**Best Practices:**
- Show the exact formatting expected
- Use clear placeholder text (e.g., "lorem", "ipsum")
- Demonstrate delimiters if applicable
- Show multiple examples if the format varies

**Example:**
```markdown
\`\`\`
{"word": "lorem", "translation": "ipsum", "definition": "dolor sit amet consectetur"}
**%%separator%%**
{"word": "lorem", "translation": "ipsum", "definition": "dolor sit amet consectetur"}
\`\`\`
```

---

### 7. IMPORTANT FORMATTING RULES (Required for Complex Outputs)
Explicit, numbered rules for output formatting.

```markdown
### IMPORTANT FORMATTING RULES:

1. [Rule 1]
2. [Rule 2]
3. [Rule 3]
```

**What to Include:**
- JSON formatting requirements
- Delimiter usage
- Line break handling
- Field requirements (exact fields, nothing extra)
- Prohibition of additional text or explanations
- Critical parsing requirements

**Best Practices:**
- Number each rule for clarity
- Be explicit about what NOT to do
- Emphasize critical rules that affect parsing
- Include warnings about common mistakes

---

### 8. ERROR HANDLING (Optional)
Define how to handle error cases.

```markdown
### ERROR HANDLING:

- If [error condition], respond with: \`ERROR_CODE\`
- If [another error condition], respond with: \`ANOTHER_ERROR_CODE\`
```

**When to Include:**
- Input validation scenarios
- Expected error states
- Fallback behaviors

**Best Practices:**
- Use clear, consistent error codes (ALL_CAPS_SNAKE_CASE)
- Specify exact error message format
- Cover common error scenarios

---

## Template Variable Conventions

### ⚠️ CRITICAL RULE: Template Variable Formatting

**ALL template variables MUST be wrapped in `**%%...%%**` (bold markdown with percent delimiters).**

This is the ONLY acceptable format for template variables throughout the entire prompt file.

### ✅ Correct Format
```markdown
**%%targetLanguage%%**
**%%proficiency%%**
**%%wordCount%%**
```

### ❌ Incorrect Formats
```markdown
%%targetLanguage%%        ❌ Missing bold markdown
**targetLanguage**        ❌ Missing percent delimiters
%targetLanguage%          ❌ Wrong delimiter
{{targetLanguage}}        ❌ Wrong delimiter
${targetLanguage}         ❌ Wrong delimiter
```

### Where to Use Template Variables

Template variables should appear in:
1. **CONTEXT section**: Define all variables here
2. **GUIDELINES section**: Reference variables when needed
3. **RESPONSE FORMAT section**: In JSDoc comments and descriptions
4. **OUTPUT STRUCTURE section**: Show delimiter variables (e.g., `**%%separator%%**`)
5. **Throughout the prompt**: Whenever referencing dynamic values

### Examples in Context

```markdown
### CONTEXT:

1. Target Learning Language: **%%targetLanguage%%**
2. Learner Proficiency Level: **%%proficiency%%**
3. Number of Words: **%%wordCount%%**

### GUIDELINES:

- Generate **%%wordCount%%** vocabulary suggestions
- All words must be in **%%targetLanguage%%**
- Appropriate for **%%proficiency%%** level

### RESPONSE FORMAT:

\`\`\`ts
interface Response {
    /** The word in **%%targetLanguage%%** */
    word: string
}
\`\`\`
```

### Naming Conventions
- Use camelCase: `%%targetLanguage%%`
- Be descriptive: Prefer `%%generativeContentLanguage%%` over `%%gcl%%`
- Maintain consistency across prompts

### Common Variables
- **Language**: `**%%targetLanguage%%**`, `**%%wordLanguage%%**`, `**%%translationLanguage%%**`
- **Proficiency**: `**%%proficiency%%**`, `**%%level%%**`
- **User Data**: `**%%userContext%%**`, `**%%existingWords%%**`, `**%%excludedWords%%**`
- **Configuration**: `**%%wordCount%%**`, `**%%difficulty%%**`, `**%%separator%%**`
- **Content**: `**%%word%%**`, `**%%words%%**`, `**%%topic%%**`, `**%%type%%**`
- **Enums**: `**%%wordTypes%%**`, `**%%wordExtraMarks%%**`, `**%%wordGenders%%**`

---

## Organization

### Directory Structure
```
prompts/
├── conversation/
│   ├── initialize_conversation.md
│   ├── respond_in_conversation.md
│   └── ...
├── games/
│   ├── generate_sentences_writing_game.md
│   ├── generate_words_typing_game.md
│   └── ...
├── words/
│   ├── generate_word_manual.md
│   ├── suggest_vocabulary.md
│   └── ...
└── guidelines.md
```

### File Naming
- Use snake_case: `generate_word_manual.md`
- Be descriptive: Name should indicate the prompt's purpose
- Group by feature domain (conversation, games, words, etc.)

---

## Best Practices Summary

### 1. Clarity
- Use clear, unambiguous language
- Avoid jargon unless defining the AI's expertise
- Provide concrete examples

### 2. Consistency
- Follow the same structure across all prompts
- Use consistent terminology and variable names
- Maintain consistent formatting
- **ALWAYS use `**%%variableName%%**` format for all variables**

### 3. Completeness
- Include all required sections
- Don't assume the AI will infer unstated requirements
- Explicitly state what NOT to do

### 4. Specificity
- Be precise about output format
- Define exact field names and types
- Specify proficiency-level variations

### 5. Validation
- Include error handling for expected edge cases
- Define validation criteria in guidelines
- Specify error response formats

### 6. Maintainability
- Use descriptive variable names
- Add comments in TypeScript interfaces
- Keep prompts focused on a single task

---

## Common Patterns

### Proficiency-Based Adjustments
```markdown
- Adjust depth and complexity based on **%%proficiency%%** level:
  - **A1-A2**: [Beginner approach]
  - **B1-B2**: [Intermediate approach]
  - **C1-C2**: [Advanced approach]
```

### Exclusion Lists
```markdown
### [SECTION NAME]:

The following [items] should be excluded (DO NOT [action]):

**%%excludedItems%%**
```

### Multi-Language Context
```markdown
1. Target Learning Language: **%%targetLanguage%%**
2. Translation Language: **%%translationLanguage%%**
3. Generative Content Language: **%%generativeContentLanguage%%**
```

### Emphasis on Specific Values
Use bold and asterisks to emphasize critical values:
```markdown
**CRITICAL**: Create the manual ONLY for the exact word "**%%word%%**" in **%%wordLanguage%%** language.
```

### Optional Fields Pattern
```markdown
/**
 * [Field description] (null if not applicable)
 */
fieldName: Type | null
```

---

## Anti-Patterns to Avoid

### ❌ Don't: Vague Instructions
```markdown
Generate some words for the user.
```

### ✅ Do: Specific Instructions
```markdown
Generate **%%wordCount%%** vocabulary suggestions appropriate for **%%proficiency%%** level.
```

---

### ❌ Don't: Unclear Output Format
```markdown
Return the results as JSON.
```

### ✅ Do: Explicit TypeScript Interface
```markdown
Return a JSON object matching this TypeScript interface:

\`\`\`ts
interface Response {
    word: string
    translation: string
}
\`\`\`
```

---

### ❌ Don't: Assume Context
```markdown
Generate vocabulary for the user.
```

### ✅ Do: Explicit Context
```markdown
### CONTEXT:

1. Target Language: **%%targetLanguage%%**
2. Proficiency Level: **%%proficiency%%**
3. Existing Vocabulary: **%%existingWords%%**
```

---

### ❌ Don't: Inconsistent Variable Names
```markdown
**%%lang%%**, **%%language%%**, **%%targetLang%%**
```

### ✅ Do: Consistent Naming
```markdown
**%%targetLanguage%%**, **%%translationLanguage%%**, **%%wordLanguage%%**
```

---

### ❌ Don't: Incorrect Variable Format
```markdown
%%targetLanguage%%        ❌ Missing bold
**targetLanguage**        ❌ Missing delimiters
%targetLanguage%          ❌ Wrong delimiters
```

### ✅ Do: Correct Variable Format
```markdown
**%%targetLanguage%%**    ✅ Bold + percent delimiters
**%%proficiency%%**       ✅ Correct format
**%%wordCount%%**         ✅ Correct format
```

---

## Testing & Iteration

When creating or modifying prompts:

1. **Verify variable format**: Ensure ALL variables use `**%%variableName%%**`
2. **Test with edge cases**: Empty lists, unusual inputs, boundary conditions
3. **Verify parsing**: Ensure delimiters and formats work correctly
4. **Check consistency**: Compare with similar prompts
5. **Validate output**: Confirm the AI follows all formatting rules
6. **Review error handling**: Test error conditions produce expected codes

---

## Examples

See existing prompts for reference:
- **Complex structured output**: `words/generate_word_manual.md`
- **Simple list generation**: `words/suggest_vocabulary.md`
- **Conversational**: `conversation/initialize_conversation.md`
- **Game generation**: `games/generate_sentences_writing_game.md`