-------------
-- Seed one conversation for root user with 9 messages, learning tips, and feedback
-- Topic: Weekend Activities and Hobbies (Casual small talk)
-- Language: English (C1 proficiency)
-- Note: AI always starts and ends the conversation (5 AI, 4 USER messages)
-------------

-- Valid UUIDs for all entities
-- Conversation ID: bef8d741-8a29-4c5e-9c1a-d5e6f7890123
-- Message IDs:
--   Message 1 (AI):   4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d1
--   Message 2 (USER): 4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d2
--   Message 3 (AI):   4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d3
--   Message 4 (USER): 4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d4
--   Message 5 (AI):   4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d5
--   Message 6 (USER): 4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d6
--   Message 7 (AI):   4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d7
--   Message 8 (USER): 4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d8
--   Message 9 (AI):   4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d9
-- Feedback IDs (for USER messages 2, 4, 6, 8):
--   Feedback 2: feed2222-bacf-4eed-bacf-eedba2feed02
--   Feedback 4: feed4444-bacf-4eed-bacf-eedba4feed04
--   Feedback 6: feed6666-bacf-4eed-bacf-eedba6feed06
--   Feedback 8: feed8888-bacf-4eed-bacf-eedba8feed08
-- Learning Tips IDs (for AI messages 1, 3, 5, 7, 9):
--   Tips 1: 71245111-7145-4714-5271-457145714571
--   Tips 3: 71245333-7145-4714-5271-457145714573
--   Tips 5: 71245555-7145-4714-5271-457145714575
--   Tips 7: 71245777-7145-4714-5271-457145714577
--   Tips 9: 71245999-7145-4714-5271-457145714579

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
     'Sarah',
     'AVATAR_ALPHA',
     'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP);

-------------
--- Messages (9 total, AI starts and ends: AI-USER-AI-USER-AI-USER-AI-USER-AI)
-------------

-- Message 1 (AI, order 1) - Conversation opener
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d1',
     1,
     'Hey there! I hope you''ve had a wonderful week. I''ve been diving into a fascinating historical novel set in medieval England - the author''s vivid descriptions really bring the period to life. What have you been up to lately? Any exciting weekend plans?',
     'AI',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL,
     CURRENT_TIMESTAMP);

-- Message 2 (USER, order 2)
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d2',
     2,
     'Hey! How was your weekend? I spent most of Saturday hiking in the mountains and it was absolutely breathtaking. Did you do anything interesting besides reading?',
     'USER',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL, -- Will be updated after feedback is created
     CURRENT_TIMESTAMP);

-- Message 3 (AI, order 3)
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d3',
     3,
     'That sounds wonderful! I''m glad you had such a refreshing experience. I actually spent most of my weekend reading, but I did take a lovely walk through the park on Sunday morning. What trail did you hike, and how challenging was it?',
     'AI',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL,
     CURRENT_TIMESTAMP);

-- Message 4 (USER, order 4)
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d4',
     4,
     'Oh, that''s sound great! I took the Eagle Peak trail, which is moderately difficult but totally worth it for the views. The weather were perfect, and I even saw some wildlife along the way. What book are you reading? I''ve been looking for something new to read myself.',
     'USER',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL, -- Will be updated after feedback is created
     CURRENT_TIMESTAMP);

-- Message 5 (AI, order 5)
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d5',
     5,
     'The trail sounds magnificent! I''d love to hear more about the wildlife you encountered - was it deer, or perhaps some birds of prey? As for the book, it''s called ''The Pillars of the Earth'' by Ken Follett. It''s quite lengthy, but utterly captivating. If you enjoy historical fiction with intricate plots and well-developed characters, I''d highly recommend it.',
     'AI',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL,
     CURRENT_TIMESTAMP);

-- Message 6 (USER, order 6)
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d6',
     6,
     'I saw couple of deer and a hawk soaring above! It was really peaceful up there. Thanks for the recommendation - I''ll definitely check it out. Do you usually prefer reading over outdoor activities, or do you like to mix it up?',
     'USER',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL, -- Will be updated after feedback is created
     CURRENT_TIMESTAMP);

-- Message 7 (AI, order 7)
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d7',
     7,
     'How marvelous! Hawks are such majestic creatures. To answer your question, I genuinely appreciate both pursuits. While I do gravitate towards reading, especially during colder months, I find that alternating between intellectual and physical activities creates a nice balance. There''s something rejuvenating about being outdoors after spending time immersed in a book.',
     'AI',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL,
     CURRENT_TIMESTAMP);

-- Message 8 (USER, order 8) - Message with many mistakes
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d8',
     8,
     'That make lot of sense! I think I''m going to started that book you mentioned tomorrow. I''m really been wanting to read more historical fiction lately, but I never have enough time because of work is so demanding. Maybe if I would wake up earlier, I could read for hour before work. Do you have any other recommendations?',
     'USER',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL, -- Will be updated after feedback is created
     CURRENT_TIMESTAMP);

-- Message 9 (AI, order 9) - Conversation closing
INSERT INTO public.conversation_messages
    (id, message_order, content, sender, conversation_id, feedback_id, created_at)
VALUES
    ('4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d9',
     9,
     'I''m delighted you''re interested in the book! Starting tomorrow sounds like a wonderful plan. Regarding your question about early morning reading, I''ve found it to be quite rewarding - there''s something peaceful about reading before the day''s demands begin. For other recommendations, if you enjoy Follett, you might also appreciate Hilary Mantel''s ''Wolf Hall'' or Bernard Cornwell''s ''The Last Kingdom'' series. Both offer rich historical detail and compelling narratives.',
     'AI',
     'bef8d741-8a29-4c5e-9c1a-d5e6f7890123',
     NULL,
     CURRENT_TIMESTAMP);

-------------
--- Feedback for USER messages (orders 2, 4, 6, 8)
-------------

-- Feedback for Message 2 (USER) - Minor mistake with good overall quality
INSERT INTO public.conversation_user_message_feedback
    (id, tutor_comment, grammar, vocabulary, answer_length, naturalness, coherence_with_context,
     register_appropriate, mistakes, strengths, suggestions, message_id, created_at)
VALUES
    ('feed2222-bacf-4eed-bacf-eedba2feed02',
     'Excellent response! Your message is warm, engaging, and perfectly appropriate for casual conversation. The use of ''absolutely breathtaking'' shows strong command of intensifiers. However, there''s a minor issue with question structure that could be improved. Overall, great conversational skills!',
     9,
     9,
     9,
     10,
     10,
     true,
     '[{"phrase": "Did you do anything interesting besides reading?", "severity": "MINOR", "errorType": "REGISTER", "explanation": "While grammatically correct, this question could be more conversational. Starting with ''How was your weekend?'' and then asking about specific activities creates better flow than jumping to a ''Did you'' question.", "correctForm": "How did you spend the rest of your weekend?"}]'::jsonb,
     '[{"phrase": "absolutely breathtaking", "strengthType": "VOCABULARY", "explanation": "Excellent use of intensifier for vivid description"}, {"phrase": "How was your weekend?", "strengthType": "FLUENCY", "explanation": "Perfect casual conversation opener with genuine enthusiasm"}, {"phrase": "Did you do anything interesting besides reading?", "strengthType": "COMMUNICATION", "explanation": "Effective use of follow-up question to maintain conversation flow"}]'::jsonb,
     '[{"original": "interesting", "suggestionType": "ENRICHMENT", "alternatives": ["eventful", "memorable", "noteworthy"], "explanation": "While ''interesting'' works well here, these alternatives can add variety and nuance to your vocabulary."}, {"original": "Did you do anything interesting besides reading?", "suggestionType": "IMPROVEMENT", "alternatives": ["What else did you get up to?", "How did you spend the rest of your weekend?"], "explanation": "These alternatives create a smoother conversational flow and sound more natural in casual English."}, {"original": "I spent most of Saturday hiking", "suggestionType": "ENRICHMENT", "alternatives": ["I dedicated most of Saturday to hiking", "I spent the better part of Saturday hiking"], "explanation": "These alternatives add sophistication while maintaining the casual tone."}]'::jsonb,
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d2',
     CURRENT_TIMESTAMP);

-- Feedback for Message 4 (USER) - Contains grammar mistakes
INSERT INTO public.conversation_user_message_feedback
    (id, tutor_comment, grammar, vocabulary, answer_length, naturalness, coherence_with_context,
     register_appropriate, mistakes, strengths, suggestions, message_id, created_at)
VALUES
    ('feed4444-bacf-4eed-bacf-eedba4feed04',
     'Good response with excellent detail! You maintained the conversational flow nicely and showed interest in your partner''s activity. However, there are a couple of grammar errors to address: ''That''s sound'' should be ''That sounds'' (subject-verb agreement), and ''The weather were'' should be ''The weather was'' (weather is singular). These are minor but important for C1 proficiency.',
     7,
     9,
     10,
     9,
     10,
     true,
     '[{"phrase": "that''s sound great", "severity": "MODERATE", "errorType": "GRAMMAR", "explanation": "After ''that'' as the subject, you need the third-person singular verb form. The contraction ''that''s'' (that is) doesn''t work with ''sound'' as a verb here. You need the verb ''sounds''.", "correctForm": "that sounds great"}, {"phrase": "The weather were perfect", "severity": "MODERATE", "errorType": "GRAMMAR", "explanation": "''Weather'' is an uncountable noun that always takes a singular verb. Even though we might think of weather as having multiple elements, grammatically it''s treated as singular.", "correctForm": "The weather was perfect"}]'::jsonb,
     '[{"phrase": "moderately difficult", "strengthType": "VOCABULARY", "explanation": "Good use of precise vocabulary to describe the challenge level"}, {"phrase": "totally worth it", "strengthType": "FLUENCY", "explanation": "Excellent use of natural informal expression"}, {"phrase": "What book are you reading?", "strengthType": "COMMUNICATION", "explanation": "Natural transition from answering about hiking to asking about the book"}]'::jsonb,
     '[{"original": "perfect", "suggestionType": "ENRICHMENT", "alternatives": ["ideal", "pristine", "glorious"], "explanation": "While ''perfect'' is great, these alternatives can make your descriptions more vivid."}, {"original": "totally worth it for the views", "suggestionType": "IMPROVEMENT", "alternatives": ["well worth it for the spectacular views", "the views made it all worthwhile"], "explanation": "Adding descriptive adjectives to ''views'' enhances your description and reduces reliance on intensifiers like ''totally''."}, {"original": "I even saw some wildlife", "suggestionType": "ENRICHMENT", "alternatives": ["I even spotted some wildlife", "I encountered some wildlife", "I came across some wildlife"], "explanation": "''Spotted'' and ''came across'' suggest unexpectedness, while ''encountered'' is more neutral"}]'::jsonb,
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d4',
     CURRENT_TIMESTAMP);

-- Feedback for Message 6 (USER) - Minor grammar mistake
INSERT INTO public.conversation_user_message_feedback
    (id, tutor_comment, grammar, vocabulary, answer_length, naturalness, coherence_with_context,
     register_appropriate, mistakes, strengths, suggestions, message_id, created_at)
VALUES
    ('feed6666-bacf-4eed-bacf-eedba6feed06',
     'Great enthusiasm and engagement! Your response effectively answers the question and maintains the conversational flow. However, there''s a grammar issue: ''couple of deer'' should be ''a couple of deer'' - the article ''a'' is needed before ''couple''. Your vocabulary choices like ''soaring'' and ''peaceful'' are excellent and show strong lexical range.',
     8,
     9,
     9,
     9,
     10,
     true,
     '[{"phrase": "I saw couple of deer", "severity": "MINOR", "errorType": "GRAMMAR", "explanation": "The phrase ''couple of'' requires the indefinite article ''a'' before it. ''Couple'' on its own means two people in a relationship, while ''a couple of'' means ''a few'' or approximately two.", "correctForm": "I saw a couple of deer"}]'::jsonb,
     '[{"phrase": "soaring above", "strengthType": "VOCABULARY", "explanation": "Excellent verb choice - vivid and precise for describing bird flight"}, {"phrase": "It was really peaceful up there", "strengthType": "FLUENCY", "explanation": "Good use of ''peaceful'' to convey the atmosphere of the experience"}, {"phrase": "Thanks for the recommendation", "strengthType": "PRAGMATICS", "explanation": "Natural acknowledgment showing politeness and engagement"}]'::jsonb,
     '[{"original": "check it out", "suggestionType": "ENRICHMENT", "alternatives": ["give it a try", "look into it", "add it to my reading list"], "explanation": "''Check it out'' is perfectly natural, but these alternatives offer variety."}, {"original": "It was really peaceful up there", "suggestionType": "IMPROVEMENT", "alternatives": ["It was incredibly peaceful up there", "The tranquility up there was remarkable"], "explanation": "''Really'' is a common intensifier. Using ''incredibly'' or rephrasing with stronger vocabulary like ''tranquility'' adds sophistication to your expression."}, {"original": "Do you usually prefer reading over outdoor activities", "suggestionType": "ENRICHMENT", "alternatives": ["Are you more of a bookworm or an outdoorsy person?", "Do you tend to lean more towards reading or being active outdoors?"], "explanation": "These alternatives range from informal (''bookworm'') to more neutral. All work well in casual conversation."}]'::jsonb,
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d6',
     CURRENT_TIMESTAMP);

-- Feedback for Message 8 (USER) - Multiple grammar mistakes of varying severity
INSERT INTO public.conversation_user_message_feedback
    (id, tutor_comment, grammar, vocabulary, answer_length, naturalness, coherence_with_context,
     register_appropriate, mistakes, strengths, suggestions, message_id, created_at)
VALUES
    ('feed8888-bacf-4eed-bacf-eedba8feed08',
     'Your message shows good enthusiasm and communicative intent, but there are several grammar issues that need attention. You have problems with subject-verb agreement (''That make''), article usage (''lot of sense'', ''for hour''), verb tense consistency (''going to started''), present perfect formation (''I''m really been wanting''), and conditional structure (''if I would wake up''). These errors impact clarity and are important to address at C1 level. Focus on reviewing basic tense formation and article usage.',
     4,
     8,
     9,
     7,
     9,
     true,
     '[{"phrase": "That make lot of sense", "severity": "MODERATE", "errorType": "GRAMMAR", "explanation": "Two errors here: (1) ''That'' is singular and requires ''makes'' not ''make''. (2) The phrase ''lot of'' needs the article ''a'' before it to be grammatically correct.", "correctForm": "That makes a lot of sense"}, {"phrase": "I''m going to started that book", "severity": "CRITICAL", "errorType": "GRAMMAR", "explanation": "After ''going to'' you need the base form of the verb (infinitive), not the past tense. ''Going to'' is already expressing future intention, so the main verb should be ''start''.", "correctForm": "I''m going to start that book"}, {"phrase": "I''m really been wanting to read", "severity": "CRITICAL", "errorType": "GRAMMAR", "explanation": "Present perfect continuous should be ''I''ve been wanting'' (have + been + -ing) not ''I''m been wanting''. You''re mixing present continuous (I''m) with present perfect continuous (have been).", "correctForm": "I''ve really been wanting to read"}, {"phrase": "I never have enough time because of work is so demanding", "severity": "MODERATE", "errorType": "GRAMMAR", "explanation": "After ''because of'' you need a noun phrase, not a clause. Since ''work is so demanding'' is a complete clause, you should use ''because'' (not ''because of''). Alternatively, restructure to ''because of how demanding work is''.", "correctForm": "I never have enough time because work is so demanding"}, {"phrase": "Maybe if I would wake up earlier", "severity": "MINOR", "errorType": "GRAMMAR", "explanation": "In first conditional statements about future possibilities, use the simple past tense in the ''if'' clause, not ''would''. ''Would'' only appears in the main clause. This is a common error even at advanced levels.", "correctForm": "Maybe if I woke up earlier"}, {"phrase": "read for hour before work", "severity": "MINOR", "errorType": "GRAMMAR", "explanation": "''Hour'' is a countable noun and requires an article. Since you''re talking about an unspecified single hour, use the indefinite article ''an'' (not ''a'' because ''hour'' starts with a vowel sound).", "correctForm": "read for an hour before work"}]'::jsonb,
     '[{"phrase": "historical fiction", "strengthType": "VOCABULARY", "explanation": "Good use of specific genre terminology"}, {"phrase": "demanding", "strengthType": "VOCABULARY", "explanation": "Appropriate adjective to describe work intensity"}]'::jsonb,
     '[{"original": "That make lot of sense", "suggestionType": "CORRECTION", "alternatives": ["That makes a lot of sense", "That makes perfect sense", "That''s very true"], "explanation": "Always ensure subject-verb agreement and include the article ''a'' before ''lot of''."}, {"original": "I''m going to started", "suggestionType": "CORRECTION", "alternatives": ["I''m going to start", "I plan to start", "I''ll start"], "explanation": "After ''going to'', always use the base form of the verb."}, {"original": "I''m really been wanting", "suggestionType": "CORRECTION", "alternatives": ["I''ve really been wanting", "I''ve been really wanting", "I''ve been meaning"], "explanation": "Use ''have'' (I''ve) not ''am'' (I''m) for present perfect continuous."}, {"original": "because of work is so demanding", "suggestionType": "CORRECTION", "alternatives": ["because work is so demanding", "since work is so demanding", "as work is so demanding"], "explanation": "Use ''because'' (not ''because of'') before a complete clause with subject and verb."}]'::jsonb,
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d8',
     CURRENT_TIMESTAMP);

-------------
--- Learning Tips for AI messages (orders 1, 3, 5, 7, 9)
-------------

-- Learning Tips for Message 1 (AI) - Conversation opener
INSERT INTO public.conversation_ai_message_learning_tips
    (id, grammar_tips, vocabulary_tips, idiom_tips, message_id, created_at)
VALUES
    ('71245111-7145-4714-5271-457145714571',
     '[{"phrase": "I''ve been diving into", "explanation": "Present perfect continuous with ''diving into'' expresses an ongoing action that started in the past and continues to the present. It emphasizes the duration and current relevance of the activity.", "grammarPoint": "Present Perfect Continuous"}, {"phrase": "bring the period to life", "explanation": "The phrase uses ''bring to life'' (make vivid/real) with the object placed before ''to life'' for emphasis. This is an example of a separable phrasal verb construction.", "grammarPoint": "Separable Phrasal Verb"}]'::jsonb,
     '[{"word": "vivid", "definition": "Producing powerful feelings or strong, clear images in the mind; intensely deep or bright", "usageNote": "Often used to describe descriptions, memories, colors, or imagination. Collocates well with ''description'', ''memory'', ''image'', ''color''.", "proficiencyLevel": "B2"}, {"word": "diving into", "definition": "To start doing something enthusiastically or with complete commitment", "usageNote": "Informal expression commonly used with books, projects, or new activities. More casual than ''beginning'' or ''starting''.", "proficiencyLevel": "C1"}]'::jsonb,
     '[{"phrase": "What have you been up to?", "meaning": "A casual way to ask someone about their recent activities or experiences. Implies friendly interest in what they''ve been doing.", "example": "Hey! Long time no see. What have you been up to? / I called to check in - what have you been up to lately?"}]'::jsonb,
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d1',
     CURRENT_TIMESTAMP);

-- Learning Tips for Message 3 (AI)
INSERT INTO public.conversation_ai_message_learning_tips
    (id, grammar_tips, vocabulary_tips, idiom_tips, message_id, created_at)
VALUES
    ('71245333-7145-4714-5271-457145714573',
     '[{"phrase": "I''m glad you had such a refreshing experience", "explanation": "Using ''such a'' before an adjective-noun combination adds emphasis and emotional connection. It''s more expressive than simply saying ''a refreshing experience''.", "grammarPoint": "Intensifier ''Such a'' with Adjective-Noun"}]'::jsonb,
     '[{"word": "refreshing", "definition": "Giving new energy or vigor; pleasantly new or different", "usageNote": "Often used to describe experiences, drinks, or perspectives that restore energy or provide welcome change. Collocates with ''experience'', ''change'', ''approach''.", "proficiencyLevel": "B2"}, {"word": "lovely", "definition": "Very beautiful, pleasant, or enjoyable", "usageNote": "A versatile adjective common in British English. Can describe weather, experiences, people, or things. More warm and personal than ''nice''.", "proficiencyLevel": "B1"}]'::jsonb,
     '[{"phrase": "I actually spent", "meaning": "Using ''actually'' here adds a conversational tone and slight emphasis, often introducing information that might be slightly unexpected or clarifying.", "example": "I actually prefer tea over coffee. / She actually lives quite close to here."}]'::jsonb,
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d3',
     CURRENT_TIMESTAMP);

-- Learning Tips for Message 5 (AI)
INSERT INTO public.conversation_ai_message_learning_tips
    (id, grammar_tips, vocabulary_tips, idiom_tips, message_id, created_at)
VALUES
    ('71245555-7145-4714-5271-457145714575',
     '[{"phrase": "I''d love to hear more", "explanation": "''Would love to'' is the conditional form expressing polite desire or preference. It''s softer and more polite than ''want to'', making it ideal for requests and expressing interest.", "grammarPoint": "Conditional for Polite Requests"}]'::jsonb,
     '[{"word": "magnificent", "definition": "Extremely beautiful, elaborate, or impressive; splendid", "usageNote": "A stronger alternative to ''beautiful'' or ''great''. Often used for landscapes, architecture, or performances. More formal than ''amazing''.", "proficiencyLevel": "C1"}, {"word": "utterly", "definition": "Completely and without qualification; absolutely", "usageNote": "An intensifier used before adjectives to emphasize totality. More emphatic than ''very'' or ''really''. Common in both speech and writing.", "proficiencyLevel": "B2"}]'::jsonb,
     '[{"phrase": "birds of prey", "meaning": "A bird that hunts and feeds on other animals (eagles, hawks, owls, etc.). The phrase ''of prey'' indicates they are predatory hunters.", "example": "The sanctuary rehabilitates injured birds of prey before releasing them back into the wild."}]'::jsonb,
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d5',
     CURRENT_TIMESTAMP);

-- Learning Tips for Message 7 (AI)
INSERT INTO public.conversation_ai_message_learning_tips
    (id, grammar_tips, vocabulary_tips, idiom_tips, message_id, created_at)
VALUES
    ('71245777-7145-4714-5271-457145714577',
     '[{"phrase": "I do gravitate towards", "explanation": "Using ''do'' before the main verb adds emphasis in affirmative statements. It strengthens the assertion while maintaining politeness. The verb ''gravitate towards'' takes the continuous form after modal constructions.", "grammarPoint": "Emphatic ''Do'' with Main Verb"}]'::jsonb,
     '[{"word": "gravitate towards", "definition": "To be naturally attracted to or drawn to something; to move or tend to move toward", "usageNote": "Metaphorical usage based on gravitational pull. Used for preferences, tendencies, or natural inclinations. More sophisticated than ''prefer'' or ''like''.", "proficiencyLevel": "C1"}, {"word": "rejuvenating", "definition": "Making someone or something look or feel younger, fresher, or more lively", "usageNote": "Often used for activities, experiences, or treatments that restore energy or vitality. Common collocations: ''rejuvenating experience'', ''rejuvenating sleep'', ''rejuvenating break''.", "proficiencyLevel": "C1"}]'::jsonb,
     '[{"phrase": "creates a nice balance", "meaning": "To establish or achieve equilibrium between different elements. ''Balance'' here refers to a healthy mix or proportion between contrasting activities or aspects of life.", "example": "She creates a nice balance between work and family time. / Finding a balance between study and relaxation is important."}]'::jsonb,
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d7',
     CURRENT_TIMESTAMP);

-- Learning Tips for Message 9 (AI) - Conversation closing
INSERT INTO public.conversation_ai_message_learning_tips
    (id, grammar_tips, vocabulary_tips, idiom_tips, message_id, created_at)
VALUES
    ('71245999-7145-4714-5271-457145714579',
     '[{"phrase": "I''m delighted you''re interested", "explanation": "Using ''delighted'' with the present continuous (you''re interested) expresses pleasure about a current state. This construction is more sophisticated than ''I''m happy that you''re interested''.", "grammarPoint": "Adjective + that-clause"}]'::jsonb,
     '[{"word": "delighted", "definition": "Feeling or showing great pleasure; very pleased", "usageNote": "Stronger and more formal than ''happy'' or ''glad''. Commonly used in polite or enthusiastic responses. Collocates well with ''to'' + infinitive or ''that'' clauses.", "proficiencyLevel": "B2"}, {"word": "rewarding", "definition": "Providing satisfaction; worthwhile", "usageNote": "Often used to describe activities or experiences that provide personal satisfaction despite requiring effort. Common with ''find it rewarding'', ''prove rewarding''.", "proficiencyLevel": "B2"}, {"word": "compelling", "definition": "Evoking interest, attention, or admiration in a powerfully irresistible way", "usageNote": "Frequently used to describe narratives, arguments, or evidence that strongly engage or convince. More sophisticated than ''interesting''.", "proficiencyLevel": "C1"}]'::jsonb,
     '[{"phrase": "the day''s demands", "meaning": "The responsibilities, tasks, or pressures that a day brings. Using the possessive form (day''s) makes this more concise and sophisticated.", "example": "She meditates before the day''s demands take over. / The morning routine helps prepare for the day''s demands."}]'::jsonb,
     '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d9',
     CURRENT_TIMESTAMP);

-------------
--- Link feedback to USER messages via feedback_id
-------------

UPDATE public.conversation_messages
SET feedback_id = 'feed2222-bacf-4eed-bacf-eedba2feed02'
WHERE id = '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d2';

UPDATE public.conversation_messages
SET feedback_id = 'feed4444-bacf-4eed-bacf-eedba4feed04'
WHERE id = '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d4';

UPDATE public.conversation_messages
SET feedback_id = 'feed6666-bacf-4eed-bacf-eedba6feed06'
WHERE id = '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d6';

UPDATE public.conversation_messages
SET feedback_id = 'feed8888-bacf-4eed-bacf-eedba8feed08'
WHERE id = '4a1b2c3d-e4f5-4a1b-2c3d-e4f5a1b2c3d8';
