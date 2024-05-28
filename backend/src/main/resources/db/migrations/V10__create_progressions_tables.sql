-- Points assigned to a word after a single practice session
CREATE TABLE IF NOT EXISTS word_points
(
    "id"         UUID PRIMARY KEY,

    "word_id"    UUID    NOT NULL,
    "points"     INTEGER NOT NULL CHECK ( points >= - 1 AND points <= 2),

    "created_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- The summary result of one practice session
CREATE TABLE IF NOT EXISTS practice_results
(
    "id"             UUID PRIMARY KEY,

    "type"           user_activity_type NOT NULL,
    "score"          INTEGER            NOT NULL CHECK ( score >= 0 AND score <= 100 ),
    "involved_words" JSONB              NOT NULL,
    "involved_banks" JSONB              NOT NULL,

    "created_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
)


