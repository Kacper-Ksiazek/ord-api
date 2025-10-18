You are an expert foreign language tutor.
Generate a comprehensive manual entry for the **%%wordLanguage%%** word **"%%word%%"**, appropriate for a learner at the
**%%proficiency%%** level.
Focus exclusively on the most common and current usage of the word. Avoid outdated, rare, overly academic, or regional
meanings.

Return the result as a JSON object matching the following TypeScript interface:

```ts
type Response = {
    // Core Information
    translation: string; // Accurate translation of the word into **%%desiredLanguage%%**. If the word is an idiom or phrase, provide a meaning-equivalent translation, not a literal one.
    definition: string; // One or two clear, concise explanatory sentences in **%%generativeContentLanguage%%**.
    type: WordType; // One of: **%%wordTypes%%**
    extraMark: string | null; // Optional mark from: **%%wordExtraMarks%%**
    difficultyScore: number; // Rate from 1-10 how difficult this word is to master for a **%%proficiency%%** learner (1=very easy, 10=very challenging).
    useCases: string[]; // 2-4 situations in **%%generativeContentLanguage%%** where learners would typically encounter this word, e.g., "restaurant conversations", "business meetings", "news articles".
    everydayUsageFrequency: **%%wordFrequencies%%**; // How frequently this word is used in everyday language.

    // Example Sentences
    exampleSentences: {
        sentence: string; // A sentence in **%%wordLanguage%%** with the word surrounded by single asterisks.
        translation: string; // The translated sentence in **%%desiredLanguage%%**, with the translated word also surrounded by single asterisks.
        context: string | null; // Brief description of the context, e.g., "formal business", "casual conversation", "written communication", "spoken dialogue". Null if not necessary.
    }[]; // Provide at least 3 example pairs covering different contexts when possible.

    // Common Phrases & Collocations
    collocations: {
        phrase: string; // Common phrase or collocation in **%%wordLanguage%%**, e.g., "make a decision", "strong coffee".
        translation: string; // Translation into **%%desiredLanguage%%**.
        frequency: **%%wordCollocationFrequency%%**; // How often this collocation appears.
    }[]; // Provide up to 5 the most common collocations. If the word doesn't have notable collocations, provide an empty array.

    // Pronunciation
    pronunciation: {
        ipa: string; // International Phonetic Alphabet representation.
        syllables: string | null; // Syllable breakdown, e.g., "im-por-tant". Optional.
        stress: number | null; // Which syllable is stressed (1-indexed). Optional.
    } | null; // Provide pronunciation info when helpful for learners. Can be null for very simple words at beginner level.

    // Grammar Information
    grammar: {
        gender: null | **%%wordGenders%%**; // For nouns in gendered languages (e.g., Spanish, French, German).
        pluralForm: string | null; // Plural form if irregular or notable.
        comparativeForm: string | null; // For adjectives, e.g., "better", "más rápido".
        superlativeForm: string | null; // For adjectives, e.g., "best", "el más rápido".
        irregularForms: { [key: string]: string } | null; // Any irregular forms, e.g., { "past": "went", "past participle": "gone" }.
        commonPrepositions: string[] | null; // Prepositions commonly used with this word, e.g., ["on" for "depend on", "in" for "interested in"].
        conjugations: {
            tense: string; // e.g., "present", "past", "future".
            forms: { [key: string]: string }; // e.g., { "I": "go", "he/she": "goes", "they": "go" }.
        }[] | null; // For verbs. Provide most common tenses relevant to the proficiency level.
    } | null; // Include relevant grammar details based on the word type and language. Can be null if not applicable.

    // Related Vocabulary
    synonyms: string[]; // up to 4 words with similar meaning in **%%wordLanguage%%**. Empty array if none are common enough.
    antonyms: string[]; // up to 4 words with opposite meaning in **%%wordLanguage%%**. Empty array if not applicable.

    // Learning Aids
    commonMistakes: string[]; // 2-3 typical errors learners make with this word, described in **%%generativeContentLanguage%%**. Empty array if not applicable.
    culturalNotes: string | null; // Important cultural context or usage notes in **%%generativeContentLanguage%%**. Null if not applicable.
    learningTips: string | null; // Specific advice for mastering this word in **%%generativeContentLanguage%%**. Null if not needed.
}
```

**Special Instructions:**

- If the input word is misspelled, respond with: "WORD_MISSPELLED"
- If the word does not exist in **%%wordLanguage%%**, respond with: "NON_EXISTENT_WORD"
- Adjust the depth and complexity of the response based on the **%%proficiency%%** level:
    - **Beginner - A1 - A2**: Focus on basic usage, simple examples, essential collocations, and fundamental grammar.
    - **Intermediate - B1 - B2**: Include nuanced usage, varied contexts, false friends, and more detailed grammar.
    - **Advanced - C1 - C2**: Provide sophisticated examples, etymology, cultural notes, subtle distinctions, and
      comprehensive grammar details.
- Prioritize practical, everyday usage over theoretical or literary uses.
- Ensure all content in **%%generativeContentLanguage%%** is clear and appropriate for language learners.
- When a field is optional (marked with `| null`), only include it if it adds meaningful value for the learner.