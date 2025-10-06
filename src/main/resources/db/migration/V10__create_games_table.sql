CREATE TABLE IF NOT EXISTS ongoing_games
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    proper_answers TEXT            NOT NULL,

    type           game_type       NOT NULL,
    language       language_name   NOT NULL,
    difficulty     game_difficulty NOT NULL,

    user_id        UUID            NOT NULL REFERENCES users (id) ON DELETE CASCADE,

    created_at     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS finished_games
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    duration   varchar(8)      NOT NULL,
    accuracy   REAL            NOT NULL CHECK ( accuracy >= 0 AND accuracy <= 1 ),
    score      INTEGER         NOT NULL,

    type       game_type       NOT NULL,
    language   language_name   NOT NULL,
    difficulty game_difficulty NOT NULL,
    result     game_result     NOT NULL,
    grade      game_grade      NOT NULL,

    user_id    UUID            NOT NULL REFERENCES users (id) ON DELETE CASCADE,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

