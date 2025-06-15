CREATE TABLE IF NOT EXISTS user_activity_logs
(
    id              UUID PRIMARY KEY,

    user_id         UUID               NOT NULL REFERENCES users (id) ON DELETE CASCADE,

    type            user_activity_type NOT NULL,
    language        language_name      NOT NULL,

    game_difficulty game_difficulty             DEFAULT NULL,

    points INTEGER NOT NULL DEFAULT 0 CHECK ( points >= 0 ),

    created_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP
);