CREATE TABLE story_contexts
(

    "id"         UUID PRIMARY KEY,

    "title"      VARCHAR(64)        NOT NULL,
    "type"       story_context_type NOT NULL,

    -- Context of the story
    "prompt"     TEXT               NOT NULL,

    "user_id"    UUID               NOT NULL REFERENCES "users" ("id") ON DELETE CASCADE,

    "created_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);