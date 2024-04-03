CREATE TABLE IF NOT EXISTS "user_sessions"
(

    "token"      VARCHAR(255) PRIMARY KEY,

    "user_id"    UUID                     NOT NULL,
    "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE
)