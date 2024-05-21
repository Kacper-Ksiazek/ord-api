CALL create_enum_type(
        'user_activity_type',
        ARRAY [
            'WORDS_TYPING',
            'CROSSWORD',
            'GAPS_FILLING',
            'IMMERSIVE_STORY',
            'SENTENCES_WRITING'
            ]
     );

CALL create_enum_type(
        'user_activity_status',
        ARRAY [
            'IN_PROGRESS',
            'COMPLETED',
            'FAILED'
            ]
     );

CREATE TABLE IF NOT EXISTS user_activities
(
    "id"              UUID PRIMARY KEY,

    "user_id"         UUID                 NOT NULL,
    "activity_type"   user_activity_type   NOT NULL,
    "status"          user_activity_status NOT NULL DEFAULT 'IN_PROGRESS',
    "acquired_points" INTEGER              NOT NULL CHECK ( acquired_points >= 0 AND acquired_points <= 1000),

    "used_gpt_tokens" INTEGER              NOT NULL DEFAULT 0 CHECK ( used_gpt_tokens >= 0 AND used_gpt_tokens <= 1000),

    "created_at"      TIMESTAMP WITH TIME ZONE      DEFAULT CURRENT_TIMESTAMP,
    "updated_at"      TIMESTAMP WITH TIME ZONE      DEFAULT CURRENT_TIMESTAMP
);