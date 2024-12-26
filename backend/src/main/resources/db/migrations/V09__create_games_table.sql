CREATE TABLE IF NOT EXISTS games
(
    -- An id of the activity
    id          UUID PRIMARY KEY,

    type        game_type       NOT NULL,
    status      game_status     NOT NULL DEFAULT 'IN_PROGRESS',
    language    language_name   NOT NULL,
    difficulty  game_difficulty NOT NULL,
    -- In ISO 8601 format
    duration    varchar(8)      NOT NULL DEFAULT '00:00:00',
    -- In percentage as int < 0 - 100 >
    final_score INTEGER         NOT NULL CHECK ( final_score >= 0 AND final_score <= 100),

    instruction TEXT            NOT NULL,

    -- An id o the user who is doing the activity ( foreign key to the users table)
    user_id     UUID            NOT NULL REFERENCES users (id) ON DELETE CASCADE,

    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

