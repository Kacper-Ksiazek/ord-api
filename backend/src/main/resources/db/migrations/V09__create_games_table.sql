CREATE TABLE IF NOT EXISTS games
(
    -- An id of the activity
    id              UUID PRIMARY KEY,

    type            game_type       NOT NULL,
    status          game_status     NOT NULL                                                           DEFAULT 'IN_PROGRESS',
    difficulty      game_difficulty NOT NULL,
    -- In ISO 8601 format
    duration        varchar(8)      NOT NULL                                                           DEFAULT '00:00:00',
    -- In percentage < 0 - 100 >
    accuracy_rate   INTEGER         NOT NULL CHECK ( accuracy_rate >= 0 AND accuracy_rate <= 100),
    acquired_points INTEGER         NOT NULL CHECK ( acquired_points >= 0 AND acquired_points <= 1000) DEFAULT 0,

    instruction     TEXT            NOT NULL,

    -- An id o the user who is doing the activity ( foreign key to the users table)
    user_id         UUID            NOT NULL REFERENCES users (id) ON DELETE CASCADE,

    created_at      TIMESTAMP WITH TIME ZONE                                                           DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE                                                           DEFAULT CURRENT_TIMESTAMP
);

