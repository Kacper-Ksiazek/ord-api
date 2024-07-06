CREATE TABLE IF NOT EXISTS "bank_groups"
(
    "id"         UUID PRIMARY KEY,

    "name"       VARCHAR(64) NOT NULL,
    "color"      VARCHAR(6)  NOT NULL,

    "created_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS "banks"
(
    "id"          UUID PRIMARY KEY,

    "name"        VARCHAR(64)  NOT NULL,
    "description" VARCHAR(255) NOT NULL,

    "user_id"     UUID         NOT NULL,
    "group_id"    UUID                     DEFAULT NULL,

    "created_at"  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE,
    FOREIGN KEY ("group_id") REFERENCES "bank_groups" ("id") ON DELETE SET NULL
);