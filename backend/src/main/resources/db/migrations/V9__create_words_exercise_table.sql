CALL create_enum_type(
        'words_exercises_type',
        ARRAY [
            'WORDS_TYPING',
            'CROSSWORD',
            'GAPS_FILLING',
            'IMMERSIVE_STORY',
            'SENTENCES_WRITING'
            ]
     );

CALL create_enum_type(
        'words_exercises_status',
        ARRAY [
            'IN_PROGRESS',
            'COMPLETED',
            'FAILED'
            ]
     );

CREATE TABLE IF NOT EXISTS words_exercises
(
    -- An id of the activity
    "id"              UUID PRIMARY KEY,
    -- An id o the user who is doing the activity ( foreign key to the users table)
    "user_id"         UUID                   NOT NULL REFERENCES users (id) ON DELETE CASCADE,

    "type"            words_exercises_type   NOT NULL,
    "status"          words_exercises_status NOT NULL                                                           DEFAULT 'IN_PROGRESS',

    "final_score"     INTEGER                NOT NULL CHECK ( final_score >= 0 AND final_score <= 1000)         DEFAULT 0,
    "acquired_points" INTEGER                NOT NULL CHECK ( acquired_points >= 0 AND acquired_points <= 1000) DEFAULT 0,

    -- List of foreign keys to the words table
    "involved_words"  JSONB                  NOT NULL,
    -- List of foreign keys to the banks table
    "involved_banks"  JSONB                  NOT NULL,

    "created_at"      TIMESTAMP WITH TIME ZONE                                                                  DEFAULT CURRENT_TIMESTAMP,
    "updated_at"      TIMESTAMP WITH TIME ZONE                                                                  DEFAULT CURRENT_TIMESTAMP
);

