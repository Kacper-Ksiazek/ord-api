CREATE TABLE IF NOT EXISTS "user_progress"
(
    "id"              UUID PRIMARY KEY,

    "user_id"         UUID    NOT NULL REFERENCES "users" ("id") ON DELETE CASCADE,
    "game_id"         UUID    NOT NULL REFERENCES "games" ("id") ON DELETE CASCADE,

    "points_obtained" INTEGER NOT NULL         DEFAULT 0 CHECK ( "points_obtained" >= 0 ),

    "created_at"      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);