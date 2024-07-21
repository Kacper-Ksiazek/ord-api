CALL create_enum_type(
        'story_context_type',
        ARRAY [
            'EDUCATIONAL',
            'ENTERTAINMENT',
            'FUN_FACT',
            'HISTORICAL',
            'MOTIVATIONAL',
            'DAILY_CONVERSATION'
            ]
     );

CREATE TABLE story_contexts
(

    "id"         UUID PRIMARY KEY,

    "title"      VARCHAR(64)        NOT NULL,
    "type"       story_context_type NOT NULL,

    -- Context of the story
    "prompt"     TEXT               NOT NULL,


    "user_id"    UUID               NOT NULL REFERENCES "users" ("id") ON DELETE CASCADE,
    "story_id"   UUID               NOT NULL REFERENCES "stories" ("id") ON DELETE CASCADE,

    "created_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE stories
    ADD COLUMN "story_context_id" UUID NOT NULL REFERENCES "story_contexts" ("id") ON DELETE CASCADE;