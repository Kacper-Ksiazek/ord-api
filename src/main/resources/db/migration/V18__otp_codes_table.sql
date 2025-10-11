CREATE TABLE IF NOT EXISTS otp_codes
(
    id         UUID PRIMARY KEY                  DEFAULT gen_random_uuid(),

    code       TEXT UNIQUE              NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    user_email VARCHAR(255)             NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_email) REFERENCES users (email) ON DELETE CASCADE
);