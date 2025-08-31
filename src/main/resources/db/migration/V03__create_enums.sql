--- +-------------------+
--- |       USERS       |
--- +-------------------+

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

---  UserActivityType
CALL create_enum_type(
        'user_activity_type',
        ARRAY [
            'CROSSWORD_GAME_COMPLETED_FLAWLESSLY',
            'CROSSWORD_GAME_COMPLETED_WITH_MISTAKES',
            'WORDS_TYPING_GAME_COMPLETED_FLAWLESSLY',
            'WORDS_TYPING_GAME_COMPLETED_WITH_MISTAKES',
            'SENTENCES_WRITING_GAME_COMPLETED_FLAWLESSLY',
            'SENTENCES_WRITING_GAME_COMPLETED_WITH_MISTAKES',

            'GAME_QUIT',

            'WORDS_ADDED_IN_ONE_DAY_10',
            'WORDS_ADDED_IN_ONE_WEEK_50',

            'WORD_COMPLETED',
            'WORDS_COMPLETED_IN_ONE_DAY_10',
            'WORDS_COMPLETED_IN_ONE_WEEK_30'


            -- TODO: Add more options in the future
            ]
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

---  GameResult
CALL create_enum_type(
        'game_result',
        ARRAY [
            'COMPLETED',
            'CANCELLED'
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
        ARRAY ['S', 'A', 'B', 'C', 'D', 'NA']
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


--- +---------------------+
--- |    CONVERSATIONS    |
--- +---------------------+

---  ConversationMessageSender
CALL create_enum_type(
        'conversation_message_sender',
        ARRAY ['AI', 'USER']
     );

---  ConversationGoal
CALL create_enum_type(
        'conversation_goal',
        ARRAY [
            'SMALL_TALK', -- Light and casual practice
            'SCENARIO_ROLEPLAY', -- Role-based: doctor visit, interview, travel
            'EXAM_PRACTICE', -- More academic/formal conversation, useful for test prep
            'TOPIC_EXPLORATION', -- Discussing a specific topic in depth (e.g., culture, history, etc.
            'OXFORD_DEBATE' -- Structured debate with pros/cons, great for advanced learners
            ]
     );

---  ConversationTone
CALL create_enum_type(
        'conversation_tone',
        ARRAY [
            'FRIENDLY', -- Casual, upbeat, like a friend
            'FORMAL', -- Polite and structured, like a teacher
            'HUMOROUS', -- Light sarcasm and jokes (mild)
            'NEUTRAL', -- Straightforward and balanced tone
            'ENCOURAGING', -- Motivational and supportive, great for shy learners
            'CHALLENGING' -- Pushes the user with harder questions and fewer hints
            ]
     );

---  ConversationAiResponseLength
CALL create_enum_type(
        'conversation_ai_response_length',
        ARRAY [
            'BRIEF', -- Short and to the point
            'NORMAL', -- Default
            'DETAILED' -- Longer replies, good for immersive practice
            ]
     );