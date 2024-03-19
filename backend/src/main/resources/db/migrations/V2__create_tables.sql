CREATE TYPE "language_name" as ENUM (
    'Polish',
    'English',
    'German',
    'French',
    'Spanish'
    );

CREATE TYPE "language_proficiency" as ENUM (
    'A1',
    'A2',
    'B1',
    'B2',
    'C1',
    'C2'
    );

CREATE TABLE IF NOT EXISTS "users"
(
    "id"         UUID PRIMARY KEY,

    "name"       TEXT NOT NULL,
    "email"      TEXT NOT NULL,

    "created_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "email_must_be_unique" UNIQUE ("email")
);

CREATE TABLE IF NOT EXISTS "language_proficiencies"
(
    "id"          UUID PRIMARY KEY,

    "language"    language_name        NOT NULL,
    "proficiency" language_proficiency NOT NULL,
    "user_id"     UUID                 NOT NULL,

    "created_at"  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE,
    CONSTRAINT "each_user_can_have_only_one_proficiency_per_language" UNIQUE ("user_id", "language")
);
