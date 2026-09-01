-------------
-- E2E worker users (parallel Playwright isolation)
-- Idempotent: safe on every deploy including production.
-- UUIDs: UUID.nameUUIDFromBytes("ord-e2e:{email}") / "ord-e2e-proficiency:{email}"
-------------

INSERT INTO public.users (id, name, email, native_language, selected_learning_language, is_account_initialized,
                          created_at, updated_at)
VALUES ('9786f5d2-22db-30ff-9cc8-009095989baa', 'E2E Worker 0', 'e2e-ci-w0@ord.test',
        'POLISH', 'ENGLISH', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('10667597-3c05-3a8c-8689-c2862cd7e294', 'E2E Worker 1', 'e2e-ci-w1@ord.test',
        'POLISH', 'ENGLISH', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('7ed79cd7-2cf0-3180-9da7-1d1e0be41e23', 'E2E Worker 2', 'e2e-ci-w2@ord.test',
        'POLISH', 'ENGLISH', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('e50d5eb2-7c93-3021-9e8f-a983554add05', 'E2E Worker 3', 'e2e-ci-w3@ord.test',
        'POLISH', 'ENGLISH', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

INSERT INTO public.language_proficiencies (id, language, level, user_id, translate_to, generative_content_language,
                                           created_at)
VALUES ('30c1a394-4540-31e9-a659-86f6901ad2c9', 'ENGLISH', 'B1', '9786f5d2-22db-30ff-9cc8-009095989baa', 'POLISH',
        'ENGLISH', CURRENT_TIMESTAMP),
       ('8746cc34-c958-3e74-9298-06e26fb4cbed', 'ENGLISH', 'B1', '10667597-3c05-3a8c-8689-c2862cd7e294', 'POLISH',
        'ENGLISH', CURRENT_TIMESTAMP),
       ('d61854e9-301e-3957-80ff-3bad816f28da', 'ENGLISH', 'B1', '7ed79cd7-2cf0-3180-9da7-1d1e0be41e23', 'POLISH',
        'ENGLISH', CURRENT_TIMESTAMP),
       ('f05522b8-cad7-31c0-bec0-1d85c7df6b17', 'ENGLISH', 'B1', 'e50d5eb2-7c93-3021-9e8f-a983554add05', 'POLISH',
        'ENGLISH', CURRENT_TIMESTAMP)
ON CONFLICT (user_id, language) DO NOTHING;
