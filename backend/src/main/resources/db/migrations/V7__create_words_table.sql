CALL create_enum_type(
        'word_type',
        ARRAY ['NOUN', 'VERB', 'ADJECTIVE', 'ADVERB', 'IDIOM', 'PHRASE']
     );

CALL create_enum_type(
        'word_extra_mark',
        ARRAY ['OFFENSIVE', 'SLANG', 'FORMAL', 'INFORMAL', 'SCIENTIFIC', 'TECHNICAL', 'LEGAL', 'MEDICAL', 'COLLOQUIAL', 'POETIC']
     );

CREATE TABLE IF NOT EXISTS "words"
(
    "id"                UUID PRIMARY KEY,

    -- Describes word's kind, such as `noun` or `verb`
    "type"              word_type     NOT NULL,

    -- Describes the word's extra mark, such as `offensive` or `slang`
    "extra_mark"        word_extra_mark          DEFAULT NULL,

    -- This is the word which is being translated | example: `book`
    "origin"            varchar(255)  NOT NULL,

    -- This is the word translated into desired language | example: `książka`
    "translation"       VARCHAR(255)  NOT NULL,

    -- Short and concise one or two sentences long definition of the word
    definition          VARCHAR(255)  NOT NULL,

    -- Set<String> of use cases of the word such as for word "dystopia" it could be "Used to describe a society characterized by human misery"
    use_cases           JSONB         NOT NULL,

    -- Describes the original language of the word | example: `ENGLISH`
    "translated_from"   language_name NOT NULL,

    -- Describes the language to which the word has been translated to | `example: POLISH`
    "translated_to"     language_name NOT NULL,

    -- If true then the word will not be included in any games and exercises
    "is_completed"      BOOLEAN       NOT NULL   DEFAULT FALSE,

    -- If set to true, the word is marked as bookmarked and therefore can be access more easily
    "is_bookmarked"     BOOLEAN       NOT NULL   DEFAULT FALSE,

    -- The number of points gathered by the user during participating in different exercises
    "points"            INTEGER       NOT NULL   DEFAULT 0,

    -- The total number of tokens used to generate examples, manuals, explanations, etc.
    "used_gpt_tokens"   INTEGER       NOT NULL   DEFAULT 0 CHECK ( "used_gpt_tokens" >= 0 ),

    -- Set<ExampleSentence> of example sentences which are used to explain the word in context
    "example_sentences" JSONB         NOT NULL,

    "user_id"           UUID          NOT NULL REFERENCES "users" ("id") ON DELETE CASCADE,
    "bank_id"           UUID                     DEFAULT NULL REFERENCES "banks" ("id") ON DELETE SET NULL,

    -- This property is not a FK, because it is only used to streamline the process of fetching words from the same bank group
    "bank_group_id"     UUID                     DEFAULT NULL,

    "created_at"        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "each_user_can_have_only_one_word_with_same_word" UNIQUE ("user_id", "origin")
)