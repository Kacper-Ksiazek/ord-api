CREATE TABLE IF NOT EXISTS quickly_added_words
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id    UUID          NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    word       VARCHAR(255)  NOT NULL,
    language   language_name NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
)