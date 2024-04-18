CALL create_enum_type(
        'bookmark_type',
        ARRAY ['WORD', 'SENTENCE', 'BANK']
     );

CREATE TABLE IF NOT EXISTS "bookmarks"
(
    "id"                    UUID PRIMARY KEY,

    "user_id"               UUID          NOT NULL,
    "bookmarked_subject_id" UUID          NOT NULL,
    "type"                  bookmark_type NOT NULL,

    "created_at"            TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"            TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE,
    CONSTRAINT "unique_bookmark" UNIQUE ("user_id", "bookmarked_subject_id", "type")
)