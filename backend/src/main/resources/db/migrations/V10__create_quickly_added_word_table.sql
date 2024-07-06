CREATE TABLE IF NOT EXISTS "quickly_added_words"
(
    "id"                UUID PRIMARY KEY,

    "user_id"           UUID          NOT NULL REFERENCES "users" ("id") ON DELETE CASCADE,
    "typed_word"        VARCHAR(255)  NOT NULL,
    "typed_in_language" language_name NOT NULL,

    "created_at"        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
)