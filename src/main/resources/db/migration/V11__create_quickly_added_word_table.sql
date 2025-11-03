CREATE TABLE IF NOT EXISTS quickly_added_words
(
    id          UUID PRIMARY KEY         DEFAULT gen_random_uuid(),

    user_id     UUID          NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    word        VARCHAR(255)  NOT NULL,
    language    language_name NOT NULL,

    translation VARCHAR(255)             DEFAULT NULL,
    definition  TEXT                     DEFAULT NULL,
    extra_mark  word_extra_mark          DEFAULT NULL,
    type        word_type                DEFAULT NULL,

    is_approved BOOLEAN       NOT NULL   DEFAULT FALSE,

    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
)