-------------
-- Seed one conversation for root user with 6 messages, learning tips, and feedback
-- Topic: Weekend Activities and Hobbies (Casual small talk)
-- Language: English (C1 proficiency)
-------------

-- Valid UUIDs for all entities
-- Conversation ID: bef8d741-8a29-4c5e-9c1a-d5e6f7890123
-- Message IDs:
--   Message 1: 4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d1
--   Message 2: 4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d2
--   Message 3: 4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d3
--   Message 4: 4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d4
--   Message 5: 4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d5
--   Message 6: 4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d6
-- Feedback IDs:
--   Feedback 1: feed1111-bacf-4eed-bacf-eedba1feed01
--   Feedback 3: feed3333-bacf-4eed-bacf-eedba3feed03
--   Feedback 5: feed5555-bacf-4eed-bacf-eedba5feed05
-- Learning Tips IDs:
--   Tips 2: 71245222-7145-4714-5271-457145714572
--   Tips 4: 71245444-7145-4714-5271-457145714574
--   Tips 6: 71245666-7145-4714-5271-457145714576

-------------
--- Conversation
-------------
INSERT INTO public.conversations
    (id, topic, additional_context, language, proficiency_level, type, ai_tone,
     ai_interlocutor_name, ai_interlocutor_avatar_id, user_id, created_at, updated_at)
VALUES
    ('bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     'Weekend Activities and Hobbies',
     NULL,
     'ENGLISH',
     'C1',
     'SMALL_TALK',
     'FRIENDLY',
     NULL,
     NULL,
     'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP);

-------------
--- Messages (6 total, alternating USER/AI)
-------------

-- Message 1 (USER, order 1)
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d1',
     1,
     'Hey! How was your weekend? I spent most of Saturday hiking in the mountains and it was absolutely breathtaking. Did you do anything interesting?',
     'USER',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL, -- Will be updated after feedback is created
     CURRENT_TIMESTAMP);

-- Message 2 (AI, order 2)
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d2',
     2,
     'That sounds wonderful! I''m glad you had such a refreshing experience. I actually spent my weekend diving into a new book I''ve been meaning to read for ages - it''s a fascinating historical novel set in medieval England. The author''s vivid descriptions really bring the period to life. What trail did you hike, and how challenging was it?',
     'AI',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL,
     CURRENT_TIMESTAMP);

-- Message 3 (USER, order 3)
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d3',
     3,
     'Oh, that''s sound great! I took the Eagle Peak trail, which is moderately difficult but totally worth it for the views. The weather were perfect, and I even saw some wildlife along the way. What book are you reading? I''ve been looking for something new to read myself.',
     'USER',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL, -- Will be updated after feedback is created
     CURRENT_TIMESTAMP);

-- Message 4 (AI, order 4)
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d4',
     4,
     'The trail sounds magnificent! I''d love to hear more about the wildlife you encountered - was it deer, or perhaps some birds of prey? As for the book, it''s called ''The Pillars of the Earth'' by Ken Follett. It''s quite lengthy, but utterly captivating. If you enjoy historical fiction with intricate plots and well-developed characters, I''d highly recommend it.',
     'AI',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL,
     CURRENT_TIMESTAMP);

-- Message 5 (USER, order 5)
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d5',
     5,
     'I saw couple of deer and a hawk soaring above! It was really peaceful up there. Thanks for the recommendation - I''ll definitely check it out. Do you usually prefer reading over outdoor activities, or do you like to mix it up?',
     'USER',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL, -- Will be updated after feedback is created
     CURRENT_TIMESTAMP);

-- Message 6 (AI, order 6)
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d6',
     6,
     'How marvelous! Hawks are such majestic creatures. To answer your question, I genuinely appreciate both pursuits. While I do gravitate towards reading, especially during colder months, I find that alternating between intellectual and physical activities creates a nice balance. There''s something rejuvenating about being outdoors after spending time immersed in a book.',
     'AI',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL,
     CURRENT_TIMESTAMP);

-------------
--- Feedback for USER messages (orders 1, 3, 5)
-------------

-- Feedback for Message 1 (USER) - Perfect message, no mistakes
INSERT INTO public.conversation_user_message_feedback
    (id, tutor_comment, grammar, vocabulary, answer_length, naturalness, coherence_with_context,
     register_appropriate, mistakes, strengths_identified, vocabulary_enrichment,
     alternative_expressions, cultural_note, message_id, created_at)
VALUES
    ('feed1111-bacf-4eed-bacf-eedba1feed01',
     'Excellent opening! Your message is warm, engaging, and perfectly appropriate for casual conversation. The use of ''absolutely breathtaking'' shows strong command of intensifiers. Your grammar and structure are flawless, and the question at the end effectively encourages continued dialogue.',
     10,
     9,
     9,
     10,
     10,
     true,
     '[]'::jsonb,
     '["Excellent use of intensifier ''absolutely breathtaking'' for vivid description", "Perfect casual conversation opener with genuine enthusiasm", "Effective use of follow-up question to maintain conversation flow", "Natural and engaging tone throughout"]'::jsonb,
     '[{"original": "interesting", "suggestion": "eventful / memorable / noteworthy", "explanation": "While ''interesting'' works well here, these alternatives can add variety and nuance to your vocabulary. ''Eventful'' emphasizes activity, ''memorable'' highlights lasting impression, and ''noteworthy'' suggests significance."}]'::jsonb,
     '[{"context": "Did you do anything interesting?", "alternatives": ["What did you get up to?", "How did you spend your weekend?", "Did you have any plans?", "What have you been up to?"], "note": "All of these are equally casual and natural for this context"}]'::jsonb,
     NULL,
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d1',
     CURRENT_TIMESTAMP);

-- Feedback for Message 3 (USER) - Contains grammar mistakes
INSERT INTO public.conversation_user_message_feedback
    (id, tutor_comment, grammar, vocabulary, answer_length, naturalness, coherence_with_context,
     register_appropriate, mistakes, strengths_identified, vocabulary_enrichment,
     alternative_expressions, cultural_note, message_id, created_at)
VALUES
    ('feed3333-bacf-4eed-bacf-eedba3feed03',
     'Good response with excellent detail! You maintained the conversational flow nicely and showed interest in your partner''s activity. However, there are a couple of grammar errors to address: ''That''s sound'' should be ''That sounds'' (subject-verb agreement), and ''The weather were'' should be ''The weather was'' (weather is singular). These are minor but important for C1 proficiency.',
     7,
     9,
     10,
     9,
     10,
     true,
     '[{"phrase": "that''s sound great", "severity": 2, "errorType": "GRAMMAR_AGREEMENT", "explanation": "After ''that'' as the subject, you need the third-person singular verb form. The contraction ''that''s'' (that is) doesn''t work with ''sound'' as a verb here. You need the verb ''sounds''.", "correctForm": "that sounds great"}, {"phrase": "The weather were perfect", "severity": 2, "errorType": "GRAMMAR_AGREEMENT", "explanation": "''Weather'' is an uncountable noun that always takes a singular verb. Even though we might think of weather as having multiple elements, grammatically it''s treated as singular.", "correctForm": "The weather was perfect"}]'::jsonb,
     '["Good use of ''moderately difficult'' to precisely describe the challenge level", "Natural transition from answering about hiking to asking about the book", "Effective detail about wildlife adds color to the narrative", "Excellent use of ''totally worth it'' - very natural informal expression"]'::jsonb,
     '[{"original": "perfect", "suggestion": "ideal / pristine / glorious", "explanation": "While ''perfect'' is great, these alternatives can make your descriptions more vivid. ''Ideal'' suggests exactly right conditions, ''pristine'' emphasizes purity/clarity, and ''glorious'' adds emotional impact."}]'::jsonb,
     '[{"context": "I even saw some wildlife", "alternatives": ["I even spotted some wildlife", "I encountered some wildlife", "I came across some wildlife", "I was lucky enough to see some wildlife"], "note": "''Spotted'' and ''came across'' suggest unexpectedness, while ''encountered'' is more neutral"}]'::jsonb,
     'In English-speaking hiking culture, it''s common to share specific trail names and difficulty ratings, as you did excellently. Wildlife sightings are often highlighted as special moments, which you captured well.',
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d3',
     CURRENT_TIMESTAMP);

-- Feedback for Message 5 (USER) - Minor grammar mistake
INSERT INTO public.conversation_user_message_feedback
    (id, tutor_comment, grammar, vocabulary, answer_length, naturalness, coherence_with_context,
     register_appropriate, mistakes, strengths_identified, vocabulary_enrichment,
     alternative_expressions, cultural_note, message_id, created_at)
VALUES
    ('feed5555-bacf-4eed-bacf-eedba5feed05',
     'Great enthusiasm and engagement! Your response effectively answers the question and maintains the conversational flow. However, there''s a grammar issue: ''couple of deer'' should be ''a couple of deer'' - the article ''a'' is needed before ''couple''. Your vocabulary choices like ''soaring'' and ''peaceful'' are excellent and show strong lexical range.',
     8,
     9,
     9,
     9,
     10,
     true,
     '[{"phrase": "I saw couple of deer", "severity": 1, "errorType": "MISSING_WORD", "explanation": "The phrase ''couple of'' requires the indefinite article ''a'' before it. ''Couple'' on its own means two people in a relationship, while ''a couple of'' means ''a few'' or approximately two.", "correctForm": "I saw a couple of deer"}]'::jsonb,
     '["Excellent verb choice ''soaring'' - vivid and precise for describing bird flight", "Good use of ''peaceful'' to convey the atmosphere of the experience", "Natural acknowledgment with ''Thanks for the recommendation''", "Well-structured question at the end that invites detailed response"]'::jsonb,
     '[{"original": "check it out", "suggestion": "give it a try / look into it / add it to my reading list", "explanation": "''Check it out'' is perfectly natural, but these alternatives offer variety. ''Give it a try'' emphasizes willingness to experiment, ''look into it'' suggests research, and ''add it to my reading list'' is specific to books."}]'::jsonb,
     '[{"context": "Do you usually prefer reading over outdoor activities", "alternatives": ["Are you more of a bookworm or an outdoorsy person?", "Do you tend to lean more towards reading or being active outdoors?", "Would you say you''re more into books or outdoor pursuits?", "What do you enjoy more - reading or being outdoors?"], "note": "These alternatives range from informal (''bookworm'') to more neutral. All work well in casual conversation."}]'::jsonb,
     'Wildlife observation is a valued part of hiking culture in English-speaking countries. Sharing these encounters, as you did, is very natural and appreciated in outdoor recreation conversations.',
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d5',
     CURRENT_TIMESTAMP);

-------------
--- Learning Tips for AI messages (orders 2, 4, 6)
-------------

-- Learning Tips for Message 2 (AI)
INSERT INTO public.conversation_ai_message_learning_tips
    (id, grammar_tips, vocabulary_tips, idiom_tips, message_id, created_at)
VALUES
    ('71245222-7145-4714-5271-457145714572',
     '[{"phrase": "I''ve been meaning to read", "explanation": "Present perfect continuous with ''meaning to'' expresses an intention that started in the past and continues to the present. It emphasizes duration of the unfulfilled intention.", "grammarPoint": "Present Perfect Continuous with Modal Intention"}, {"phrase": "bring the period to life", "explanation": "The phrase uses ''bring to life'' (make vivid/real) with the object placed before ''to life'' for emphasis. This is an example of a separable phrasal verb construction.", "grammarPoint": "Separable Phrasal Verb"}]'::jsonb,
     '[{"word": "vivid", "definition": "Producing powerful feelings or strong, clear images in the mind; intensely deep or bright", "usageNote": "Often used to describe descriptions, memories, colors, or imagination. Collocates well with ''description'', ''memory'', ''image'', ''color''.", "proficiencyLevel": "B2"}, {"word": "diving into", "definition": "To start doing something enthusiastically or with complete commitment", "usageNote": "Informal expression commonly used with books, projects, or new activities. More casual than ''beginning'' or ''starting''.", "proficiencyLevel": "C1"}]'::jsonb,
     '[{"phrase": "for ages", "meaning": "For a very long time. Literally means ''for many ages/eras'', but figuratively used to emphasize extended duration in casual conversation.", "example": "I haven''t seen her for ages! / They''ve been friends for ages."}]'::jsonb,
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d2',
     CURRENT_TIMESTAMP);

-- Learning Tips for Message 4 (AI)
INSERT INTO public.conversation_ai_message_learning_tips
    (id, grammar_tips, vocabulary_tips, idiom_tips, message_id, created_at)
VALUES
    ('71245444-7145-4714-5271-457145714574',
     '[{"phrase": "I''d love to hear more", "explanation": "''Would love to'' is the conditional form expressing polite desire or preference. It''s softer and more polite than ''want to'', making it ideal for requests and expressing interest.", "grammarPoint": "Conditional for Polite Requests"}]'::jsonb,
     '[{"word": "magnificent", "definition": "Extremely beautiful, elaborate, or impressive; splendid", "usageNote": "A stronger alternative to ''beautiful'' or ''great''. Often used for landscapes, architecture, or performances. More formal than ''amazing''.", "proficiencyLevel": "C1"}, {"word": "utterly", "definition": "Completely and without qualification; absolutely", "usageNote": "An intensifier used before adjectives to emphasize totality. More emphatic than ''very'' or ''really''. Common in both speech and writing.", "proficiencyLevel": "B2"}]'::jsonb,
     '[{"phrase": "birds of prey", "meaning": "A bird that hunts and feeds on other animals (eagles, hawks, owls, etc.). The phrase ''of prey'' indicates they are predatory hunters.", "example": "The sanctuary rehabilitates injured birds of prey before releasing them back into the wild."}]'::jsonb,
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d4',
     CURRENT_TIMESTAMP);

-- Learning Tips for Message 6 (AI)
INSERT INTO public.conversation_ai_message_learning_tips
    (id, grammar_tips, vocabulary_tips, idiom_tips, message_id, created_at)
VALUES
    ('71245666-7145-4714-5271-457145714576',
     '[{"phrase": "I do gravitate towards", "explanation": "Using ''do'' before the main verb adds emphasis in affirmative statements. It strengthens the assertion while maintaining politeness. The verb ''gravitate towards'' takes the continuous form after modal constructions.", "grammarPoint": "Emphatic ''Do'' with Main Verb"}]'::jsonb,
     '[{"word": "gravitate towards", "definition": "To be naturally attracted to or drawn to something; to move or tend to move toward", "usageNote": "Metaphorical usage based on gravitational pull. Used for preferences, tendencies, or natural inclinations. More sophisticated than ''prefer'' or ''like''.", "proficiencyLevel": "C1"}, {"word": "rejuvenating", "definition": "Making someone or something look or feel younger, fresher, or more lively", "usageNote": "Often used for activities, experiences, or treatments that restore energy or vitality. Common collocations: ''rejuvenating experience'', ''rejuvenating sleep'', ''rejuvenating break''.", "proficiencyLevel": "C1"}]'::jsonb,
     '[{"phrase": "creates a nice balance", "meaning": "To establish or achieve equilibrium between different elements. ''Balance'' here refers to a healthy mix or proportion between contrasting activities or aspects of life.", "example": "She creates a nice balance between work and family time. / Finding a balance between study and relaxation is important."}]'::jsonb,
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d6',
     CURRENT_TIMESTAMP);

-------------
--- Link feedback to USER messages via feedback_id
-------------

UPDATE public.conversation_messages
SET feedback_id = 'feed1111-bacf-4eed-bacf-eedba1feed01'
WHERE id = '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d1';

UPDATE public.conversation_messages
SET feedback_id = 'feed3333-bacf-4eed-bacf-eedba3feed03'
WHERE id = '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d3';

UPDATE public.conversation_messages
SET feedback_id = 'feed5555-bacf-4eed-bacf-eedba5feed05'
WHERE id = '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d5';
