-------------
-- Make AI interlocutor fields non-nullable in conversations table
-------------

ALTER TABLE conversations
    ALTER COLUMN ai_interlocutor_name SET NOT NULL,
    ALTER COLUMN ai_interlocutor_avatar_id SET NOT NULL;
