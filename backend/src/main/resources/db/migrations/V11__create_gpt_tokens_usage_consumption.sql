CALL create_enum_type(
        'gpt_consumption_type',
        ARRAY [
            'GENERATE_WORDS_EXERCISES',
            'GENERATE_MANUAL_FOR_SINGLE_WORD',
            'GENERATE_WRITING_TOPIC',
            'CHECK_WRITING',
            'CHECK_WORDS_EXERCISES'
            ]
     );

CREATE TABLE IF NOT EXISTS gpt_tokens_consumption
(
    id               UUID PRIMARY KEY,

    user_id          UUID                 REFERENCES users (id) ON DELETE SET NULL,
    consumption_type gpt_consumption_type NOT NULL,

    -- Number of Open AI tokens consumed
    number_of_tokens INTEGER              NOT NULL CHECK (number_of_tokens >= 0),

    "created_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);