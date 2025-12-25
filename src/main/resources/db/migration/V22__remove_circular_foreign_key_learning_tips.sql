-- Remove circular foreign key relationship between conversation_messages and conversation_ai_message_learning_tips
-- The relationship is now one-way: learning tips reference messages via message_id

ALTER TABLE conversation_messages
    DROP CONSTRAINT IF EXISTS fk_message_learning_tips_id;

ALTER TABLE conversation_messages
    DROP COLUMN IF EXISTS learning_tips_id;
