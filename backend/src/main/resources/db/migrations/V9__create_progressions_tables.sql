CALL create_enum_type(
        'practice_type',
        ARRAY ['CROSSWORD', 'SENTENCES_WRITING', 'WORDS_TYPING', 'GAPS']
     );

CREATE TABLE IF NOT EXISTS word_points
(
    "word_id" UUID    NOT NULL,
    "points"  INTEGER NOT NULL CHECK ( points >= - 1 AND points <= 2)
);

CREATE TABLE IF NOT EXISTS practice_results
(
    "id"             UUID PRIMARY KEY,

    "type"           practice_type NOT NULL,
    "score"          INTEGER       NOT NULL CHECK ( score >= 0 AND score <= 100 ),
    "involved_words" JSONB         NOT NULL,
    "involved_banks" JSONB         NOT NULL,

    "created_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
)


