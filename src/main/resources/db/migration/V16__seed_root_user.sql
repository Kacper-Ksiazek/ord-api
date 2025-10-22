-------------
--- users
-------------
INSERT INTO public.users (id, name, email, native_language, selected_learning_language, is_account_initialized,
                          created_at, updated_at)
VALUES ('aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'Kacper Książek', 'kacper.b.ksiazek@gmail.com',
        'POLISH', 'ENGLISH', true,
        '2025-06-15 14:33:06.851385 +00:00', '2025-06-15 14:33:06.851387 +00:00');


-------------
-- language_proficiencies
-------------
INSERT INTO public.language_proficiencies (id, language, level, user_id, generative_content_language, created_at)
VALUES ('17963c02-5627-4c10-b0e6-55eb1c6a1c61', 'ENGLISH', 'C1', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'ENGLISH',
        '2025-06-15 14:33:06.854763 +00:00');
INSERT INTO public.language_proficiencies (id, language, level, user_id, generative_content_language, created_at)
VALUES ('9a3df940-52f4-4d5e-ba36-b2fb6e4715ff', 'GERMAN', 'A2', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'ENGLISH',
        '2025-06-15 14:33:06.857153 +00:00');
INSERT INTO public.language_proficiencies (id, language, level, user_id, generative_content_language, created_at)
VALUES ('110fbc1d-f265-4905-b0e8-5cc64e9a33e8', 'SLOVENIAN', 'A1', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'ENGLISH',
        '2025-06-15 14:33:06.859346 +00:00');

-------------
-- bank groups
-------------
INSERT INTO public.bank_groups (id, name, color, user_id, created_at, updated_at)
VALUES ('4c574cf5-fb00-4285-9e72-eab699c90970', 'Yellow Valley', '#FFD700', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2025-06-15 14:33:06.880136 +00:00', '2025-06-15 14:33:06.880140 +00:00');
INSERT INTO public.bank_groups (id, name, color, user_id, created_at, updated_at)
VALUES ('0389c54f-cccc-4f41-bc19-bf2af0191209', 'Blue Ocean', '#0000FF', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2025-06-15 14:33:06.881363 +00:00', '2025-06-15 14:33:06.881367 +00:00');
INSERT INTO public.bank_groups (id, name, color, user_id, created_at, updated_at)
VALUES ('59ba22c3-0099-4f40-98f0-59c3df4b6d8a', 'Green Forest', '#008000', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2025-06-15 14:33:06.882265 +00:00', '2025-06-15 14:33:06.882268 +00:00');
INSERT INTO public.bank_groups (id, name, color, user_id, created_at, updated_at)
VALUES ('a239f7a6-8ca9-4a59-945a-8e9b20926764', 'Red Desert', '#FF0000', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2025-06-15 14:33:06.882878 +00:00', '2025-06-15 14:33:06.882880 +00:00');

-------------
-- banks
-------------
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('1ac13fa3-b08b-4982-88ae-da61dce3c354', 'Valley November 2024',
        'Words associated with the Valley, accumulated in November 2024', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '4c574cf5-fb00-4285-9e72-eab699c90970', '2025-06-15 14:33:06.893907 +00:00',
        '2025-06-15 14:33:06.893910 +00:00');
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('caec18c4-3e3c-43c7-8c98-ebefcb0659b7', 'Valley December 2024',
        'Words associated with the Valley, accumulated in December 2024', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '4c574cf5-fb00-4285-9e72-eab699c90970', '2025-06-15 14:33:06.894716 +00:00',
        '2025-06-15 14:33:06.894718 +00:00');
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('172bca8f-7386-4dbd-b3d1-60dcce573ec4', 'Ocean June 2025',
        'Words associated with the Ocean, accumulated in June 2025', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', '2025-06-15 14:33:06.895270 +00:00',
        '2025-06-15 14:33:06.895273 +00:00');
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('929f555b-f372-48df-a9f7-7a4ec628d79e', 'Ocean July 2025',
        'Words associated with the Ocean, accumulated in July 2025', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', '2025-06-15 14:33:06.895821 +00:00',
        '2025-06-15 14:33:06.895823 +00:00');
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('2f3afc16-e1be-44d6-aab0-5b1363e22ec3', 'Forest August 2025',
        'Words associated with the Forest, accumulated in August 2025', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', '2025-06-15 14:33:06.896361 +00:00',
        '2025-06-15 14:33:06.896363 +00:00');
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('bcefd281-0385-412c-9c83-31484e56dd8c', 'Forest September 2025',
        'Words associated with the Forest, accumulated in September 2025', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', '2025-06-15 14:33:06.896894 +00:00',
        '2025-06-15 14:33:06.896896 +00:00');
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('edb81261-1f1d-432a-9436-1d9303add361', 'Desert October 2025',
        'Words associated with the Desert, accumulated in October 2025', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', '2025-06-15 14:33:06.897409 +00:00',
        '2025-06-15 14:33:06.897411 +00:00');
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('8e3a727a-908a-4c06-8ff5-d508f6868f90', 'Desert November 2025',
        'Words associated with the Desert, accumulated in November 2025', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', '2025-06-15 14:33:06.897851 +00:00',
        '2025-06-15 14:33:06.897853 +00:00');

-------------
-- words
-------------
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('6ab0a01f-97eb-4114-8ba2-8f4a657e94cd', 'NOUN', null, 'boardwalk', 'bulevard',
        'A *boardwalk* is a wooden walkway, often found near beaches, used for pedestrian traffic and entertainment.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '8e3a727a-908a-4c06-8ff5-d508f6868f90',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:06.983653 +00:00',
        '2025-06-15 14:33:06.983658 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('e1e401da-0224-4356-bcdc-c89e5237f3c8', 'NOUN', null, 'beehive', 'ul',
        'A *beehive* is a structure where bees live and produce honey.', 'ENGLISH', 'POLISH', false, true, 0,
        'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:06.985073 +00:00',
        '2025-06-15 14:33:06.985075 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('907af8cc-c68e-4c11-b506-bbfa7545bc61', 'VERB', null, 'mutter', 'mamroczyć',
        'To speak quietly and indistinctly, often expressing dissatisfaction.', 'ENGLISH', 'POLISH', false,
        false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:06.985881 +00:00',
        '2025-06-15 14:33:06.985883 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('43166a14-fc87-480c-8f4b-4661d3c7aa2a', 'ADJECTIVE', null, 'sluggish', 'ospale',
        'The word ''sluggish'' describes a state of being slow or lacking energy.', 'ENGLISH', 'POLISH',
        false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:06.986633 +00:00',
        '2025-06-15 14:33:06.986635 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('a0992a09-7586-48dc-8b85-d7fa6365565a', 'ADJECTIVE', null, 'lethargic', 'letargiczny',
        'The word describes a state of sluggishness or lack of energy. It is often used to describe a person who is inactive or apathetic.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:06.987352 +00:00',
        '2025-06-15 14:33:06.987354 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('93e418c8-b601-4117-a0d8-83c08d59a0ca', 'ADJECTIVE', null, 'ephemeral', 'ephemeralny',
        'Something that lasts for a very short time.', 'ENGLISH', 'POLISH', false, false, 0,
        'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'edb81261-1f1d-432a-9436-1d9303add361',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:06.988006 +00:00',
        '2025-06-15 14:33:06.988008 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('2fb8b299-0199-4e9d-a720-5b44fba977a7', 'ADJECTIVE', null, 'transient', 'przejrzysty',
        'Transient refers to something temporary or lasting for a short period of time.', 'ENGLISH', 'POLISH',
        false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'caec18c4-3e3c-43c7-8c98-ebefcb0659b7',
        '4c574cf5-fb00-4285-9e72-eab699c90970', null, '2025-06-15 14:33:06.988655 +00:00',
        '2025-06-15 14:33:06.988657 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('3acf17a4-770f-4d99-928b-4cb113f94355', 'ADJECTIVE', null, 'fleeting', 'ulotny',
        'Something that lasts for a very short time or is difficult to hold onto.', 'ENGLISH', 'POLISH',
        false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:06.989442 +00:00',
        '2025-06-15 14:33:06.989452 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('d92400eb-077d-4d7f-a095-9b850c44106f', 'VERB', null, 'depletes', 'wyczerpuje',
        'To diminish the quantity or supply of something; to exhaust resources or energy.', 'ENGLISH',
        'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2f3afc16-e1be-44d6-aab0-5b1363e22ec3',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:06.990228 +00:00',
        '2025-06-15 14:33:06.990232 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('1ed73d97-9755-4ef6-bfc5-39de627706b6', 'IDIOM', null, 'don''t shoot them down in flames',
        'nie zestrzelaj ich w płomieniach',
        'This idiom means to harshly criticize or dismiss someone''s ideas or efforts, often in a way that can be humiliating.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'edb81261-1f1d-432a-9436-1d9303add361',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:06.990929 +00:00',
        '2025-06-15 14:33:06.990932 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('d32becba-9815-47e6-b18a-0a5b2c9b7909', 'IDIOM', null, 'fruit of thought', 'owoce myśli',
        'The phrase ''fruit of thought'' refers to the results or outcomes of contemplation, reflection, or intellectual effort.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:06.991540 +00:00',
        '2025-06-15 14:33:06.991543 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('17d9c6e7-adfd-4a64-a1e2-eff9c9adebed', 'IDIOM', null, 'down the rabbit hole',
        'w dół króliczej nory',
        'The phrase ''down the rabbit hole'' refers to a situation that becomes increasingly complex or bizarre, often leading to unexpected outcomes.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:06.992074 +00:00',
        '2025-06-15 14:33:06.992079 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('f3b391f5-91d0-4a5f-b8da-db906c812f3d', 'ADJECTIVE', null, 'well-to-do', 'zamożny',
        'A term used to describe someone who is wealthy or affluent.', 'ENGLISH', 'POLISH', false, false,
        0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:06.992581 +00:00',
        '2025-06-15 14:33:06.992584 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('4cea91f7-7ed7-4722-8044-280573fb8ade', 'ADJECTIVE', null, 'across-the-board', 'ogólny',
        'The term ''across-the-board'' refers to something that applies universally or to all parts or members without exception.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:06.993075 +00:00',
        '2025-06-15 14:33:06.993078 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('b1898c39-791a-48f3-ab3a-3ef7e03d8fad', 'ADJECTIVE', null, 'run-of-the-mill', 'przeciętny',
        'The term describes something that is ordinary or of average quality, lacking any special features or characteristics.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:06.993569 +00:00',
        '2025-06-15 14:33:06.993572 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('05afa286-5935-4cd4-a9a1-a51038149eb3', 'ADJECTIVE', null, 'state-of-the-art',
        'najnowocześniejszy',
        'The term refers to the most advanced or sophisticated technology, methods, or design currently available.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:06.994106 +00:00',
        '2025-06-15 14:33:06.994213 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('82f7cb46-3e87-433a-a4d5-c5fa2eaf7eff', 'ADJECTIVE', null, 'off-the-cuff', 'z marszu',
        'The term ''off-the-cuff'' refers to something said or done spontaneously, without preparation.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:06.994772 +00:00',
        '2025-06-15 14:33:06.994775 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('a9be3be1-4d41-43a1-b8ee-be4d0cb41417', 'IDIOM', null, 'larger-than-life',
        'osobowość większa niż życie',
        'This phrase describes someone or something that is notably impressive or larger than the ordinary, often in a charismatic or dramatic way.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '8e3a727a-908a-4c06-8ff5-d508f6868f90',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:06.995239 +00:00',
        '2025-06-15 14:33:06.995242 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('d4673027-c4f5-4ace-8caa-64ca1ea560bb', 'ADJECTIVE', null, 'middle-of-the-road', 'umiarkowany',
        'The term ''middle-of-the-road'' describes something that is moderate or not extreme.', 'ENGLISH',
        'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:06.995661 +00:00',
        '2025-06-15 14:33:06.995664 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('a331d7b8-9da4-46fd-a3ed-47fb3e62927d', 'ADVERB', null, 'though-out', 'pomimo',
        'The word ''though'' is used to indicate a contrast with something previously stated or understood.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:06.996100 +00:00',
        '2025-06-15 14:33:06.996102 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('530b52d4-006e-4af5-ad2d-0cb656a4bbfc', 'ADJECTIVE', null, 'paid-for', 'płatny',
        'The term ''paid-for'' refers to something that has been purchased or is compensated by payment.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '1ac13fa3-b08b-4982-88ae-da61dce3c354',
        '4c574cf5-fb00-4285-9e72-eab699c90970', null, '2025-06-15 14:33:06.996522 +00:00',
        '2025-06-15 14:33:06.996525 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('80256eeb-7765-4c23-9fce-9ee67096670a', 'PHRASE', null, 'take-it-or-leave-it',
        'bierz albo zostaw',
        'A phrase indicating that a person must accept the terms offered or forgo the opportunity altogether.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:06.996925 +00:00',
        '2025-06-15 14:33:06.996927 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('57679f5b-de4c-4730-8af9-71d42cfdd015', 'VERB', null, 'go through', 'przejrzeć',
        'To examine or consider something thoroughly, often as part of a process.', 'ENGLISH', 'POLISH',
        false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:07.004391 +00:00',
        '2025-06-15 14:33:07.004394 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('9027edba-d197-4968-acda-ea6627d2b440', 'PHRASE', null, 'business-as-usual',
        'normalne funkcjonowanie',
        'The term ''business-as-usual'' refers to the usual or expected activities and processes in an organization, especially during unusual circumstances.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'caec18c4-3e3c-43c7-8c98-ebefcb0659b7',
        '4c574cf5-fb00-4285-9e72-eab699c90970', null, '2025-06-15 14:33:06.997297 +00:00',
        '2025-06-15 14:33:06.997300 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('1bc487fd-8aae-4ffa-8cd8-caeac700068a', 'PHRASE', null, 'all-you-can-eat',
        'wszystko, co możesz zjeść',
        'An offer at a restaurant where customers can eat as much food as they want for a set price.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '172bca8f-7386-4dbd-b3d1-60dcce573ec4',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:06.997745 +00:00',
        '2025-06-15 14:33:06.997748 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('28f8f190-046a-413b-9706-efab42539f0b', 'VERB', null, 'meagre', 'fuzja',
        'To *merge* means to combine two or more entities into one.', 'ENGLISH', 'POLISH', false, false,
        0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '1ac13fa3-b08b-4982-88ae-da61dce3c354',
        '4c574cf5-fb00-4285-9e72-eab699c90970', null, '2025-06-15 14:33:06.998250 +00:00',
        '2025-06-15 14:33:06.998253 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('c659818a-ca32-40e4-aac9-33386432027a', 'NOUN', null, 'firm', 'firma',
        'A firm is a business or company, often used to refer specifically to one providing professional services.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '8e3a727a-908a-4c06-8ff5-d508f6868f90',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:06.998661 +00:00',
        '2025-06-15 14:33:06.998663 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('fc4aa25a-7c25-4236-b9bc-c821cb32105d', 'ADJECTIVE', null, 'doting', 'roztkliwiony',
        'Doting refers to showing excessive love or fondness, often in a way that can be considered overly indulgent.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'edb81261-1f1d-432a-9436-1d9303add361',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:06.999079 +00:00',
        '2025-06-15 14:33:06.999081 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('912a72e4-6dd4-4b2e-aad4-5d5c69418042', 'ADJECTIVE', null, 'bluntly', 'szczery',
        'To speak in a straightforward and honest manner, often without regard for others'' feelings.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:06.999485 +00:00',
        '2025-06-15 14:33:06.999487 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('db1c9042-bf1b-42e3-b453-f238d4590518', 'IDIOM', null, 'to add insult to injury',
        'przykładać sól do rany',
        'To add insult to injury means to make a bad situation even worse by saying or doing something that is hurtful.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:06.999869 +00:00',
        '2025-06-15 14:33:06.999871 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('f5a378cf-dc63-4785-95a0-c7a93ee665d5', 'IDIOM', null, 'to cap it all', 'a na dodatek',
        'This phrase is used to emphasize an additional, often negative, fact that culminates a series of events or situations.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:07.000326 +00:00',
        '2025-06-15 14:33:07.000328 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('19fd0bdc-6404-4d24-aee3-aefba0687ef0', 'ADJECTIVE', null, 'flustered', 'roztrzęsiony',
        'To be flustered means to be in a state of agitated confusion or nervous excitement.', 'ENGLISH',
        'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:07.000953 +00:00',
        '2025-06-15 14:33:07.000956 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('7ef0d8e0-a5df-43d9-8d2c-baf16cb30ab6', 'ADJECTIVE', null, 'blunt', 'tępy',
        'The word ''blunt'' typically describes an object with a dull edge or point, or a person who speaks in a straightforward, often harsh manner.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'edb81261-1f1d-432a-9436-1d9303add361',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:07.001641 +00:00',
        '2025-06-15 14:33:07.001651 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('fb58b90a-4b7e-418c-a225-732451467c04', 'IDIOM', null, 'out of the blue', 'znikąd',
        'The phrase ''out of the blue'' refers to something unexpected or surprising that happens suddenly.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'edb81261-1f1d-432a-9436-1d9303add361',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:07.002245 +00:00',
        '2025-06-15 14:33:07.002250 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('5c1ed01f-2e2c-4eb7-a8ea-479f21b91dc2', 'VERB', null, 'mingle', 'mieszać',
        'To mingle means to mix or combine with others in a social setting.', 'ENGLISH', 'POLISH', false,
        true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'edb81261-1f1d-432a-9436-1d9303add361',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:07.002620 +00:00',
        '2025-06-15 14:33:07.002624 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('cadf2f13-c0b9-46dd-b803-e4e11fdbe024', 'VERB', null, 'hover', 'unosić się',
        'To remain suspended in the air without making physical contact with a surface.', 'ENGLISH', 'POLISH',
        false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:07.002955 +00:00',
        '2025-06-15 14:33:07.002957 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('1532ee60-8388-4882-8f22-82dad6ac96e5', 'NOUN', 'FORMAL', 'pleasantry', 'uprzejmość',
        'A pleasantry is a remark or action that is polite or friendly, often used to create a pleasant conversation atmosphere.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '1ac13fa3-b08b-4982-88ae-da61dce3c354',
        '4c574cf5-fb00-4285-9e72-eab699c90970', null, '2025-06-15 14:33:07.003373 +00:00',
        '2025-06-15 14:33:07.003376 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('a9e851e6-c7b8-4d70-a285-5d8584735f28', 'IDIOM', null, 'pick someone''s brain',
        'wyciągnąć kogoś na rozmowę',
        'To ''pick someone''s brain'' means to seek advice or information from someone, often about their expertise or knowledge.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2f3afc16-e1be-44d6-aab0-5b1363e22ec3',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:07.003933 +00:00',
        '2025-06-15 14:33:07.003937 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('1703194b-10c6-4b7e-a973-d9ec3b04b0ed', 'NOUN', null, 'redundancy', 'nadmiar',
        'Redundancy refers to the state of being not or no longer needed or useful; the presence of extra components that are not necessary.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:07.004795 +00:00',
        '2025-06-15 14:33:07.004799 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('427c18c0-c8c3-4245-ba82-a63eee6dd987', 'NOUN', null, 'upward trend', 'tendencja wzrostowa',
        'An upward trend refers to a general increase or improvement in a particular situation over a period of time.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'edb81261-1f1d-432a-9436-1d9303add361',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:07.005364 +00:00',
        '2025-06-15 14:33:07.005368 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('fdd5aa1a-79d9-490a-8aab-c028fc98e350', 'NOUN', null, 'tipping point', 'punkt zwrotny',
        'A ''tipping point'' is a critical moment or threshold that leads to a significant change or event.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:07.005682 +00:00',
        '2025-06-15 14:33:07.005686 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('06f55bd8-3153-45ce-ae43-bbe05c59f3b3', 'IDIOM', null, 'to be on the up', 'mieć się lepiej',
        'To be in a better state, improving or recovering.', 'ENGLISH', 'POLISH', false, false, 0,
        'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:07.006011 +00:00',
        '2025-06-15 14:33:07.006013 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('978a5f4e-666f-4523-8fe4-423b95e884e9', 'ADVERB', null, 'albeit', 'chociaż',
        'Albeit is used to introduce a contrasting statement or to acknowledge something while still recognizing a limitation or an exception.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:07.006348 +00:00',
        '2025-06-15 14:33:07.006352 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('21106d94-d625-41ae-9e61-e3c27b42ee58', 'ADJECTIVE', null, 'cumbersome', 'niewygodny',
        'The word describes something that is large, heavy, or complicated, making it difficult to handle or manage.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:07.006677 +00:00',
        '2025-06-15 14:33:07.006680 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('98c2e981-3904-48dc-b892-978716d95a57', 'VERB', 'LEGAL', 'infringe', 'naruszyć',
        'To infringe means to violate or encroach upon a law, agreement, or right.', 'ENGLISH', 'POLISH',
        false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '172bca8f-7386-4dbd-b3d1-60dcce573ec4',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:07.006970 +00:00',
        '2025-06-15 14:33:07.006972 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('dbeed3d7-ba5c-4a77-9512-9972336ebd8a', 'ADJECTIVE', null, 'permissible', 'dozwolony',
        'Permissible refers to something that is allowed or permitted according to rules or regulations.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'caec18c4-3e3c-43c7-8c98-ebefcb0659b7',
        '4c574cf5-fb00-4285-9e72-eab699c90970', null, '2025-06-15 14:33:07.007340 +00:00',
        '2025-06-15 14:33:07.007343 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('1b7f72a2-ee9a-4258-9681-05ef20728de6', 'ADJECTIVE', 'LEGAL', 'admissible', 'dozwolony',
        'The word ''admissible'' refers to something that is allowed or permitted, especially in a legal context.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:07.007705 +00:00',
        '2025-06-15 14:33:07.007709 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('bd980035-cab9-4b51-9aa0-2c429ec3ca3c', 'NOUN', null, 'spur', 'bodziec',
        'A *spur* is something that encourages or motivates someone to take action or to do something.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:07.008104 +00:00',
        '2025-06-15 14:33:07.008108 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('69b5ddeb-e688-476e-9e90-e0b223be0c52', 'NOUN', null, 'brick-and-mortar', 'sklep stacjonarny',
        'A ''brick-and-mortar'' refers to a physical retail store that operates from a traditional building, as opposed to an online store.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '1ac13fa3-b08b-4982-88ae-da61dce3c354',
        '4c574cf5-fb00-4285-9e72-eab699c90970', null, '2025-06-15 14:33:07.008419 +00:00',
        '2025-06-15 14:33:07.008422 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('c2c51bc2-d3a1-4bfb-83f3-488dd85159f8', 'NOUN', null, 'certainty', 'pewność',
        'A state of being sure, confident, or certain about something.', 'ENGLISH', 'POLISH', false,
        false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:07.008796 +00:00',
        '2025-06-15 14:33:07.008799 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('fe21fe74-b689-4dfb-a2be-1e4d0f85c828', 'ADJECTIVE', null, 'palpable', 'namacalny',
        'The word describes something that is so intense or obvious that it can be physically felt or perceived.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null,
        '2025-06-15 14:33:07.009269 +00:00',
        '2025-06-15 14:33:07.009276 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('34ad29df-1ab0-4450-9852-2239aeea4e9c', 'VERB', null, 'lull', 'uspokajać',
        'To lull means to calm someone or something, usually by soothing sounds or actions.', 'ENGLISH',
        'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:07.009703 +00:00',
        '2025-06-15 14:33:07.009707 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('f6151878-c7b8-4141-8885-cb12552dbfd7', 'VERB', null, 'ruminate', 'rozmyślać',
        'To ruminate means to think deeply about something or to consider various aspects before making a decision.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:07.010030 +00:00',
        '2025-06-15 14:33:07.010033 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('3c1038c6-e343-4bbb-8e92-554e5fc02707', 'NOUN', 'MEDICAL', 'chamomile', 'rumianek',
        'Chamomile is a flowering plant known for its calming properties, often used to make herbal tea.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '8e3a727a-908a-4c06-8ff5-d508f6868f90',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:07.010332 +00:00',
        '2025-06-15 14:33:07.010334 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('87ed9d59-5da6-4b76-a0e8-21c314589ea3', 'NOUN', null, 'vocational school', 'szkoła zawodowa',
        'A vocational school is an educational institution that provides practical and skills-oriented training for specific trades or careers.',
        'ENGLISH', 'POLISH', false, false, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2f3afc16-e1be-44d6-aab0-5b1363e22ec3',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:07.010724 +00:00',
        '2025-06-15 14:33:07.010727 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, translated_from,
                          translated_to, is_completed, is_bookmarked, points, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('0a2fb6cb-eba1-404e-8888-f810be0f2f2d', 'NOUN', null, 'shed', 'szopa',
        'A *shed* is a simple, often small building used for storage or to provide shelter for tools and equipment.',
        'ENGLISH', 'POLISH', false, true, 0, 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:07.011081 +00:00',
        '2025-06-15 14:33:07.011084 +00:00');