CALL create_enum_type(
        'game_type',
        ARRAY [
            'WORDS_TYPING',
            'CROSSWORD',
            'GAPS_FILLING',
            'IMMERSIVE_STORY',
            'SENTENCES_WRITING'
            ]
     );

CALL create_enum_type(
        'game_status',
        ARRAY [
            'IN_PROGRESS',
            'COMPLETED',
            'FAILED'
            ]
     );

CREATE TABLE IF NOT EXISTS "games"
(
    -- An id of the activity
    "id"              UUID PRIMARY KEY,
    -- An id o the user who is doing the activity ( foreign key to the users table)
    "user_id"         UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,

    "type"            game_type   NOT NULL,
    "status"          game_status NOT NULL                                                           DEFAULT 'IN_PROGRESS',

    "final_score"     INTEGER     NOT NULL CHECK ( final_score >= 0 AND final_score <= 1000)         DEFAULT 0,
    "acquired_points" INTEGER     NOT NULL CHECK ( acquired_points >= 0 AND acquired_points <= 1000) DEFAULT 0,

    -- List of foreign keys to the words table
    "involved_words"  JSONB       NOT NULL,
    -- List of foreign keys to the banks table
    "involved_banks"  JSONB       NOT NULL,

    "used_gpt_tokens" INTEGER     NOT NULL                                                           DEFAULT 0 CHECK ( "used_gpt_tokens" >= 0 ),

    "created_at"      TIMESTAMP WITH TIME ZONE                                                       DEFAULT CURRENT_TIMESTAMP,
    "updated_at"      TIMESTAMP WITH TIME ZONE                                                       DEFAULT CURRENT_TIMESTAMP
);

