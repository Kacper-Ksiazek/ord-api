CREATE TABLE IF NOT EXISTS bank_groups
(
    id         UUID PRIMARY KEY         DEFAULT gen_random_uuid(),

    name       VARCHAR(64) NOT NULL,
    -- Color in hex format, e.g. #FF0000 ( 7 characters  long )
    color      VARCHAR(7)  NOT NULL,

    user_id    UUID        NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT unique_group_name UNIQUE (name, user_id)
);


CREATE TABLE IF NOT EXISTS banks
(
    id          UUID PRIMARY KEY         DEFAULT gen_random_uuid(),

    name        VARCHAR(64)  NOT NULL,
    description VARCHAR(255) NOT NULL,

    user_id     UUID         NOT NULL,
    group_id    UUID                     DEFAULT NULL,

    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES bank_groups (id) ON DELETE SET NULL,
    CONSTRAINT unique_bank_name UNIQUE (name, user_id)
);