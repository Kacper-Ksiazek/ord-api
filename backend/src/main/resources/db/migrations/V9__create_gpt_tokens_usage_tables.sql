CALL create_enum_type(
        'words_gpt_tokens_consumption_type',
        ARRAY [
            'GENERATE_SENTENCE',
            'GENERATE_ENTIRE_MANUAL'
            ]
     );

CALL create_enum_type(
        'games_gpt_tokens_consumption_type',
        ARRAY [
            'GENERATE_GAME',
            'CHECK_WRITTEN_SENTENCES'
            ]
     );

CREATE TABLE IF NOT EXISTS "word_tokens_usages"
(
    id               UUID PRIMARY KEY,

    user_id          UUID                              REFERENCES users (id) ON DELETE SET NULL,
    words_id         UUID                              REFERENCES words (id) ON DELETE SET NULL,

    consumption_type words_gpt_tokens_consumption_type NOT NULL,

    -- Number of Open AI tokens consumed
    number_of_tokens INTEGER                           NOT NULL CHECK (number_of_tokens >= 0),

    "created_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "game_tokens_usages"
(
    id               UUID PRIMARY KEY,

    user_id          UUID                              REFERENCES users (id) ON DELETE SET NULL,
    games_id         UUID                              REFERENCES games (id) ON DELETE SET NULL,

    consumption_type games_gpt_tokens_consumption_type NOT NULL,

    -- Number of Open AI tokens consumed
    number_of_tokens INTEGER                           NOT NULL CHECK (number_of_tokens >= 0),

    "created_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);