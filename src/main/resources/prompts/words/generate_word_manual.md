You are an expert foreign language tutor.

Generate a manual entry for the **%%wordLanguage%%** word **"%%word%%"**, appropriate for a learner at the **%%proficiency%%** level.

Focus exclusively on the most common and current usage of the word. Avoid outdated, rare, overly academic, or regional meanings.

Return the result as a JSON object matching the following TypeScript interface:

```ts
type Response = {
    translation: string, // Accurate translation of the word into **%%desiredLanguage%%**. If the word is an idiom or phrase, provide a meaning-equivalent translation, not a literal one.
    definition: string, // One or two clear, concise explanatory sentences in **%%generativeContentLanguage%%**.
    type: WordType // One of: **%%wordTypes%%**
    extraMark: string | null // Optional mark from: **%%wordExtraMarks%%**
    useCases: string[], // Sample contexts in **%%generativeContentLanguage%%**, one per distinct meaning (if applicable).
    exampleSentences: {
        sentence: string, // A sentence in **%%wordLanguage%%** with the word surrounded by single asterisks.
        translation: string // The translated sentence in **%%desiredLanguage%%**, with the translated word also surrounded by single asterisks.
    }[] // Provide at least 3 example pairs.
}
```

If the input word is misspelled, respond with: "WORD_MISSPELLED" 
If the word does not exist in **%%wordLanguage%%**, respond with: "NON_EXISTENT_WORD"