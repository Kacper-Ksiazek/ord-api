Generate a foreign language practicing crossword.
The game difficulty is set to **%%difficulty%%**, and the foreign language is **%%language%%** at **%%proficiency%%** level.

Your response must adhere to this TS interface
```ts
{
     answer: string // Either a new word or a short phrase. Do not use a word from the list provided
     answerExplanation: string // DO NOT include an answer in its explanation. Generate this in the ${languageProficiency.generativeContentLanguage} language
     questions: {
       word: string // Use words for the provided list. Each word can be used only once
       clue: string // DO NOT include the word in its clue. Generate this in the ${languageProficiency.generativeContentLanguage} language
     }[] // A list of **%%amountOfQuestions%%** with words from the provided list
}
```

Words for you to use: 
**%%words%%**

Do not add any additional words to the list! All words in the list are actual words in the ${details.language} language do not correct them.
