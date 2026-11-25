ALTER TABLE ai_explanations
    ADD COLUMN follow_ups JSONB       NOT NULL DEFAULT '{}'::jsonb,
    ADD COLUMN kind       VARCHAR(32) DEFAULT NULL;

-- Follow-up texts move into one JSONB map keyed by follow-up action name.
UPDATE ai_explanations
SET follow_ups = jsonb_strip_nulls(jsonb_build_object(
        'SIMPLER', simpler_text,
        'MORE_EXAMPLES', more_examples_text,
        'SIMILAR_EXPRESSIONS', similar_expressions_text
                 ));

ALTER TABLE ai_explanations
    DROP COLUMN simpler_text,
    DROP COLUMN more_examples_text,
    DROP COLUMN similar_expressions_text,
    ADD CONSTRAINT chk_ai_explanations_kind CHECK (kind IS NULL OR kind IN ('WORD_OR_PHRASE', 'GRAMMAR_OR_CONCEPT'));
