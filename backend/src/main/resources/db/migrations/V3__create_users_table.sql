CALL create_enum_type(
        'user_role',
        ARRAY ['ADMIN', 'USER']
     );

CALL create_enum_type(
        'language_name',
        ARRAY [
            'POLISH',
            'ENGLISH',
            'GERMAN',
            'FRENCH',
            'SPANISH',
            'ITALIAN',
            'NORWEGIAN',
            'RUSSIAN'
            ]
     );

CREATE TABLE IF NOT EXISTS "users"
(
    "id"              UUID PRIMARY KEY,

    "name"            VARCHAR(255)  NOT NULL,
    "email"           VARCHAR(255)  NOT NULL,
    "password"        VARCHAR(255)  NOT NULL,
    "role"            user_role     NOT NULL   DEFAULT 'USER',
    "native_language" language_name NOT NULL,

    "created_at"      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "email_must_be_unique" UNIQUE ("email")
);
