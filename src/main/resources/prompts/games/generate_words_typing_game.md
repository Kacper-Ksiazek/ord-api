Create a word typing game designed for practicing vocabulary in a foreign language.
The game difficulty is set to **%%difficulty%%**, and the foreign language is **%%language%%** at **%%proficiency%%** level.

Your response must adhere to this TS interface

`Map<string, string>` where each key is a word from the provided list,
and the corresponding value is a clue that describes the word without including the word itself.

The clues should be in **%%generativeContentLanguage%%**
You will generate **%%amountOfQuestions%%** such pairs.

Output the result in the following format:

```json
{ 
    "word1": "Clue for 1st word in the specified language", 
    "word2": "Clue for 2nd word", 
    "wordN": "Clue for n-th word"
} 
```

Words for you to use:
**%%words%%**

Do not add any additional words to the list! All words in the list are actual words in the **%%language%%** language do not correct them.
