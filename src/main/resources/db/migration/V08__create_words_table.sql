CREATE TABLE IF NOT EXISTS words
(
    id              UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    type            word_type     NOT NULL,                 -- Describes word's kind, such as `noun` or `verb`
    origin          varchar(255)  NOT NULL,                 -- This is the word which is being translated | example: `book`
    translation     VARCHAR(255)  NOT NULL,                 -- This is the word translated into desired language | example: `książka`
    definition      VARCHAR(255)  NOT NULL,                 -- Short and concise one or two sentences long definition of the word
    extra_mark      word_extra_mark          DEFAULT NULL,  -- Describes the word's extra mark, such as `offensive` or `slang`

    translated_from language_name NOT NULL,                 -- Describes the original language of the word | example: `ENGLISH`
    translated_to   language_name NOT NULL,                 -- Describes the language to which the word has been translated to | `example: POLISH`

    is_completed    BOOLEAN       NOT NULL   DEFAULT FALSE, -- If set to true, the word is not used in games, exercises, etc.
    is_bookmarked   BOOLEAN       NOT NULL   DEFAULT FALSE, -- If set to true, the word is marked as bookmarked and therefore can be access more easily

    points          INTEGER       NOT NULL   DEFAULT 0,     -- The number of points gathered by the user during participating in different exercises

    user_id         UUID          NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    bank_id         UUID                     DEFAULT NULL REFERENCES banks (id) ON DELETE SET NULL,
    bank_group_id   UUID                     DEFAULT NULL,  -- This property is not a FK, because it is only used to streamline the process of fetching words from the same bank group

    completed_at    TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_origin_per_user_language_and_type UNIQUE (user_id, translated_from, origin, type)
);

CREATE INDEX idx_words_id_user_id ON words (id, user_id);
CREATE INDEX idx_words_user_language ON words (user_id, translated_from);

CREATE TABLE IF NOT EXISTS word_details
(
    id                       UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    word_id                  UUID                          NOT NULL REFERENCES words (id) ON DELETE CASCADE,

    use_cases                JSONB                         NOT NULL, -- Set<String>
    synonyms                 JSONB                         NOT NULL, -- Set<String>
    antonyms                 JSONB                         NOT NULL, -- Set<String>
    common_mistakes          JSONB                         NOT NULL, -- Set<String>

    example_sentences        JSONB                         NOT NULL, -- Set<ExampleSentence>
    collocations             JSONB                         NOT NULL, -- Set<WordCollocation>
    pronunciation            JSONB                    DEFAULT NULL,  -- WordPronunciation
    grammar                  JSONB                    DEFAULT NULL,  -- WordGrammar

    cultural_notes           TEXT                     DEFAULT NULL,  -- Additional cultural notes about the word
    learning_tips            TEXT                     DEFAULT NULL,  -- Tips to help remember or learn the word more effectively

    created_at               TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_word_details_word_id ON word_details (word_id)