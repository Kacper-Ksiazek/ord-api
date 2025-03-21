CREATE TABLE IF NOT EXISTS ongoing_games
(
    id             UUID PRIMARY KEY,

    proper_answers TEXT            NOT NULL,

    type           game_type       NOT NULL,
    language       language_name   NOT NULL,
    difficulty     game_difficulty NOT NULL,

    user_id        UUID            NOT NULL REFERENCES users (id) ON DELETE CASCADE,

    created_at     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS completed_games
(
    id          UUID PRIMARY KEY,

    duration    varchar(8)      NOT NULL,
    final_score INTEGER         NOT NULL CHECK ( final_score >= 0 AND final_score <= 100),

    type        game_type       NOT NULL,
    language    language_name   NOT NULL,
    difficulty  game_difficulty NOT NULL,
    result      game_result     NOT NULL,
    grade       game_grade      NOT NULL,

    user_id     UUID            NOT NULL REFERENCES users (id) ON DELETE CASCADE,

    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

