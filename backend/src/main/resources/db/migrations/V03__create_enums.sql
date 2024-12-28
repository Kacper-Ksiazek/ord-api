--- +-------------------+
--- |       USERS       |
--- +-------------------+

---  UserRole
CALL create_enum_type(
        'user_role',
        ARRAY ['ADMIN', 'USER']
     );

---  LanguageName
CALL create_enum_type(
        'language_name',
        ARRAY [
            'POLISH',
            'ENGLISH',
            'GERMAN',
            'FRENCH',
            'SPANISH',
            'ITALIAN',
            'NORWEGIAN',
            'RUSSIAN',
            'SLOVENIAN'
            ]
     );

---  LanguageProficiency
CALL create_enum_type(
        'language_proficiency',
        ARRAY ['A1', 'A2', 'B1', 'B2', 'C1', 'C2']
     );

--- +-------------------+
--- |       WORDS       |
--- +-------------------+

---  WordType
CALL create_enum_type(
        'word_type',
        ARRAY ['NOUN', 'VERB', 'ADJECTIVE', 'ADVERB', 'IDIOM', 'PHRASE']
     );

---  WordExtraMark
CALL create_enum_type(
        'word_extra_mark',
        ARRAY ['OFFENSIVE', 'SLANG', 'FORMAL', 'INFORMAL', 'SCIENTIFIC', 'TECHNICAL', 'LEGAL', 'MEDICAL', 'COLLOQUIAL', 'POETIC']
     );

--- +-------------------+
--- |       GAMES       |
--- +-------------------+

---  GameType
CALL create_enum_type(
        'game_type',
        ARRAY [
            'WORDS_TYPING',
            'CROSSWORD',
            'GAPS_FILLING',
            'IMMERSIVE_STORY',
            'SENTENCES_WRITING'
            ]
     );

---  GameDifficulty
CALL create_enum_type(
        'game_difficulty',
        ARRAY [
            'EASY',
            'MEDIUM',
            'HARD'
            ]
     );

---  GameStatus
CALL create_enum_type(
        'game_status',
        ARRAY [
            'IN_PROGRESS',
            'COMPLETED',
            'CANCELED',
            'PAUSED'
            ]
     );

---  StoryContextType
CALL create_enum_type(
        'story_context_type',
        ARRAY [
            'EDUCATIONAL',
            'ENTERTAINMENT',
            'FUN_FACT',
            'HISTORICAL',
            'MOTIVATIONAL',
            'DAILY_CONVERSATION'
            ]
     );

--- Grade
CALL create_enum_type(
        'game_grade',
        ARRAY ['S', 'A', 'B', 'C', 'D', 'NA' ]
     );

--- +-------------------+
--- |    GPT TOKENS     |
--- +-------------------+

---  WordsGptTokensConsumptionType
CALL create_enum_type(
        'words_gpt_tokens_consumption_type',
        ARRAY [
            'GENERATE_SENTENCE',
            'GENERATE_ENTIRE_MANUAL'
            ]
     );

---  GamesGptTokensConsumptionType
CALL create_enum_type(
        'games_gpt_tokens_consumption_type',
        ARRAY [
            'GENERATE',
            'REVIEW'
            ]
     );

---  StoriesGptTokensConsumptionType
CALL create_enum_type(
        'stories_gpt_tokens_consumption_type',
        ARRAY [
            'GENERATE_STORY_WITH_WORD_EXPLANATIONS',
            'REGENERATE_EXPLANATION_FOR_SINGLE_WORD'
            ]
     );



