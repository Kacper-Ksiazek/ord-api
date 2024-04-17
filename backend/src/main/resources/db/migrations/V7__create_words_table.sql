CALL create_enum_type(
        'word_type',
        ARRAY ['NOUN', 'VERB', 'ADJECTIVE', 'ADVERB', 'IDIOM', 'PHRASE']
     );

CREATE TABLE IF NOT EXISTS "words"
(
    "id"         UUID PRIMARY KEY,

    "type"       word_type     NOT NULL,
    "original"   language_name NOT NULL,
    "translated" VARCHAR(255)  NOT NULL,

    "bank_id"    UUID                     DEFAULT NULL,
    "user_id"    UUID          NOT NULL,

    "created_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "each_user_can_have_only_one_word_with_same_original" UNIQUE ("user_id", "original"),
    FOREIGN KEY ("bank_id") REFERENCES "banks" ("id") ON DELETE CASCADE,
    FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE
)