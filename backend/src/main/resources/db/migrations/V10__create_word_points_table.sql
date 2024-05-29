-- Points assigned to a word after a single words exercise session
CREATE TABLE IF NOT EXISTS word_points
(
    "id"         UUID PRIMARY KEY,

    "word_id"    UUID    NOT NULL,
    "points"     INTEGER NOT NULL CHECK ( points >= - 1 AND points <= 2),

    "created_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


