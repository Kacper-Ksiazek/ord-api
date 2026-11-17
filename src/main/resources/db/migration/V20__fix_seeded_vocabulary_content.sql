-------------
-- Fix seeded vocabulary (V13, V16, V17) for the root user
--
-- Why: the original seeds had systemic content problems that surface in the word details panel:
--   * use_cases held tags/part-of-speech labels ("noun", "verb", "building") instead of situations
--   * common_mistakes held usage trivia instead of actual learner mistakes
--   * details mixed several senses (e.g. "shed" as noun and verb) while the word row has one type
--   * wrong or literal translations (transient -> "przejrzysty", meagre -> "fuzja", paid-for -> "płatny")
--   * detail rows describing a different expression than the word row ("don" vs the idiom, "pick someone")
--   * example sentences without *emphasis* markers and with broken translations
--   * definitions phrased as "The word 'X' describes..." and V17 definitions with inline "Example: ..."
-------------

-- boardwalk
UPDATE public.words
SET translation = 'promenada, drewniany pomost',
    definition = 'A raised wooden path for walking, usually along a beach or across wet ground.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '6ab0a01f-97eb-4114-8ba2-8f4a657e94cd'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing a seaside town or beach holiday", "Giving directions along the waterfront", "Walking trails through wetlands or national parks"]'::jsonb,
    synonyms = '["promenade", "walkway"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Using *boardwalk* for any pavement in town — a city pavement is a *sidewalk* (US) or *pavement* (UK); a *boardwalk* is made of wooden boards.", "Saying *on the boardwalk* vs *in the boardwalk* — you walk *on* or *along* a boardwalk, never *in* it."]'::jsonb,
    example_sentences = '[{"context": "holiday", "sentence": "We walked along the *boardwalk* at sunset.", "translation": "Spacerowaliśmy *promenadą* o zachodzie słońca."}, {"context": "describing a place", "sentence": "The *boardwalk* was lined with ice-cream stands and arcades.", "translation": "Wzdłuż *promenady* stały budki z lodami i salony gier."}, {"context": "nature trail", "sentence": "A wooden *boardwalk* leads you across the marsh to the lake.", "translation": "Drewniany *pomost* prowadzi przez mokradła aż do jeziora."}]'::jsonb,
    collocations = '[{"phrase": "stroll along the boardwalk", "translation": "spacerować promenadą", "frequency": "VERY_COMMON"}, {"phrase": "seaside boardwalk", "translation": "nadmorska promenada", "frequency": "COMMON"}, {"phrase": "wooden boardwalk", "translation": "drewniany pomost", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈbɔːrdwɔːk", "syllables": "board-walk", "stress": 1}'::jsonb,
    grammar = '{"pluralForm": "boardwalks"}'::jsonb,
    cultural_notes = 'Boardwalks are a classic part of American beach towns on the East Coast, such as Atlantic City or Coney Island, and are often lined with shops, food stands and amusement rides.',
    learning_tips = 'Split it into *board* (deska) + *walk* (chodzić) — literally a path made of boards.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '6ab0a01f-97eb-4114-8ba2-8f4a657e94cd'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- beehive
UPDATE public.word_details
SET use_cases = '["Talking about beekeeping or honey", "Describing a garden or farm", "Comparing a busy place to a beehive"]'::jsonb,
    synonyms = '["hive"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Confusing *beehive* with *bee nest* — a *beehive* is usually built by people for bees; wild bees live in a *nest* or *colony*.", "Writing it as two words (*bee hive*) — the standard spelling is one word: *beehive*."]'::jsonb,
    example_sentences = '[{"context": "beekeeping", "sentence": "The beekeeper checked the *beehive* for honey.", "translation": "Pszczelarz sprawdził, czy w *ulu* jest miód."}, {"context": "garden", "sentence": "We keep two *beehives* at the end of the garden.", "translation": "Na końcu ogrodu mamy dwa *ule*."}, {"context": "figurative", "sentence": "The office was a *beehive* of activity before the launch.", "translation": "Przed premierą biuro tętniło życiem jak *ul*."}]'::jsonb,
    collocations = '[{"phrase": "a beehive of activity", "translation": "miejsce tętniące życiem", "frequency": "COMMON"}, {"phrase": "keep a beehive", "translation": "trzymać ul", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈbiːhaɪv", "syllables": "bee-hive", "stress": 1}'::jsonb,
    grammar = '{"pluralForm": "beehives"}'::jsonb,
    cultural_notes = 'A *beehive* is also a tall, rounded 1960s women''s hairstyle, which you may hear in films or descriptions of that era.',
    learning_tips = 'Polish uses the same image: a busy place is *jak ul* — English says *a beehive of activity*.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'e1e401da-0224-4356-bcdc-c89e5237f3c8'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- mutter
UPDATE public.words
SET translation = 'mamrotać, mruczeć pod nosem',
    definition = 'To say something quietly and unclearly, often because you are annoyed or complaining.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '907af8cc-c68e-4c11-b506-bbfa7545bc61'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing someone complaining quietly", "Telling a story about an awkward moment", "Writing dialogue in fiction"]'::jsonb,
    synonyms = '["mumble", "murmur", "grumble"]'::jsonb,
    antonyms = '["shout", "yell"]'::jsonb,
    common_mistakes = '["Using *mutter* and *mumble* as exact synonyms — *mutter* usually suggests annoyance or complaining, *mumble* just means speaking unclearly.", "Forgetting the preposition in *mutter under your breath* — not *mutter under breath*."]'::jsonb,
    example_sentences = '[{"context": "storytelling", "sentence": "He *muttered* something under his breath and left the room.", "translation": "*Mruknął* coś pod nosem i wyszedł z pokoju."}, {"context": "everyday complaint", "sentence": "She *muttered* about the weather all the way to the station.", "translation": "Przez całą drogę na dworzec *mamrotała* coś o pogodzie."}, {"context": "dialogue", "sentence": "\"Typical,\" he *muttered* when the train was cancelled.", "translation": "„No jasne” — *mruknął*, gdy odwołano pociąg."}]'::jsonb,
    collocations = '[{"phrase": "mutter under your breath", "translation": "mruczeć pod nosem", "frequency": "VERY_COMMON"}, {"phrase": "mutter something", "translation": "mruknąć coś", "frequency": "VERY_COMMON"}, {"phrase": "mutter darkly", "translation": "mruczeć złowrogo", "frequency": "OCCASIONAL"}]'::jsonb,
    pronunciation = '{"ipa": "ˈmʌtər", "syllables": "mut-ter", "stress": 1}'::jsonb,
    grammar = NULL,
    cultural_notes = 'In stories *mutter* often signals a character''s quiet irritation, so writers use it instead of *said* to show mood.',
    learning_tips = 'Imagine someone grumbling behind your back: *mutter, mutter, mutter* — the sound of the word imitates the action.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '907af8cc-c68e-4c11-b506-bbfa7545bc61'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- sluggish
UPDATE public.words
SET translation = 'ospały, powolny',
    definition = 'Moving, working or reacting more slowly than usual, often because of tiredness.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '43166a14-fc87-480c-8f4b-4661d3c7aa2a'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing how you feel after a heavy meal or poor sleep", "Talking about a slow computer or app", "Business news about a weak economy"]'::jsonb,
    synonyms = '["slow", "lethargic", "listless"]'::jsonb,
    antonyms = '["energetic", "lively", "brisk"]'::jsonb,
    common_mistakes = '["Using *sluggish* as an adverb (*he worked sluggish*) — the adverb is *sluggishly*.", "Using *sluggish* for a person who is simply lazy — it describes low energy or slow reaction, not unwillingness to work."]'::jsonb,
    example_sentences = '[{"context": "everyday", "sentence": "I always feel *sluggish* after a big lunch.", "translation": "Po dużym obiedzie zawsze czuję się *ospały*."}, {"context": "technology", "sentence": "My laptop has been really *sluggish* since the update.", "translation": "Od aktualizacji mój laptop działa bardzo *powoli*."}, {"context": "business news", "sentence": "Growth remained *sluggish* for most of the year.", "translation": "Przez większą część roku wzrost pozostawał *słaby*."}]'::jsonb,
    collocations = '[{"phrase": "feel sluggish", "translation": "czuć się ospałym", "frequency": "VERY_COMMON"}, {"phrase": "sluggish economy", "translation": "słaba gospodarka", "frequency": "VERY_COMMON"}, {"phrase": "sluggish growth", "translation": "powolny wzrost", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈslʌɡɪʃ", "syllables": "slug-gish", "stress": 1}'::jsonb,
    grammar = '{"comparativeForm": "more sluggish", "superlativeForm": "most sluggish"}'::jsonb,
    cultural_notes = 'Economists and journalists use *sluggish* very often for markets and sales, so it is worth knowing beyond its everyday meaning.',
    learning_tips = 'Think of a *slug* (ślimak bez skorupy) — slow and low on energy, just like *sluggish*.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '43166a14-fc87-480c-8f4b-4661d3c7aa2a'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- lethargic
UPDATE public.words
SET translation = 'ospały, apatyczny',
    definition = 'Having very little energy and no wish to do anything.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'a0992a09-7586-48dc-8b85-d7fa6365565a'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing how you feel when you are ill or exhausted", "Talking to a doctor about symptoms", "Describing a pet that seems unwell"]'::jsonb,
    synonyms = '["sluggish", "listless", "drowsy"]'::jsonb,
    antonyms = '["energetic", "lively", "alert"]'::jsonb,
    common_mistakes = '["Translating *lethargic* as *letargiczny* in every context — in everyday Polish *ospały* or *bez energii* sounds much more natural.", "Using *lethargic* for a short moment of tiredness — it suggests a stronger, longer lack of energy than *tired*."]'::jsonb,
    example_sentences = '[{"context": "weather", "sentence": "The heat made everyone *lethargic*.", "translation": "Upał sprawił, że wszyscy byli *ospali*."}, {"context": "pets", "sentence": "If your dog seems *lethargic*, take it to the vet.", "translation": "Jeśli twój pies wydaje się *apatyczny*, zabierz go do weterynarza."}, {"context": "health", "sentence": "I''ve been feeling *lethargic* since I caught the flu.", "translation": "Odkąd złapałem grypę, jestem *bez energii*."}]'::jsonb,
    collocations = '[{"phrase": "feel lethargic", "translation": "czuć się ospałym", "frequency": "VERY_COMMON"}, {"phrase": "become lethargic", "translation": "stać się apatycznym", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ləˈθɑːrdʒɪk", "syllables": "le-thar-gic", "stress": 2}'::jsonb,
    grammar = '{"comparativeForm": "more lethargic", "superlativeForm": "most lethargic"}'::jsonb,
    cultural_notes = 'Doctors and vets use *lethargic* as a symptom, so you will often hear it in medical questions like *Is the child lethargic?*',
    learning_tips = 'The noun is *lethargy*; Polish *letarg* is the same root, but means a much deeper state — so prefer *ospały* in translation.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'a0992a09-7586-48dc-8b85-d7fa6365565a'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- ephemeral
UPDATE public.words
SET translation = 'ulotny, efemeryczny',
    definition = 'Lasting for only a very short time.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '93e418c8-b601-4117-a0d8-83c08d59a0ca'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Talking about trends, fashions or fame", "Describing social media stories that disappear", "Writing about art, nature or memories"]'::jsonb,
    synonyms = '["fleeting", "short-lived", "transient"]'::jsonb,
    antonyms = '["permanent", "lasting", "enduring"]'::jsonb,
    common_mistakes = '["Using *ephemeral* in casual chat where *short-lived* or *brief* sounds more natural — it is a fairly literary word.", "Mispronouncing it with stress on the first syllable — the stress is on *-phem-*: e-PHEM-er-al."]'::jsonb,
    example_sentences = '[{"context": "social media", "sentence": "Fame on social media is often *ephemeral*.", "translation": "Sława w mediach społecznościowych bywa *ulotna*."}, {"context": "technology", "sentence": "Stories are *ephemeral* — they disappear after 24 hours.", "translation": "Relacje są *efemeryczne* — znikają po 24 godzinach."}, {"context": "art", "sentence": "She loves photographing the *ephemeral* beauty of cherry blossoms.", "translation": "Uwielbia fotografować *ulotne* piękno kwitnących wiśni."}]'::jsonb,
    collocations = '[{"phrase": "ephemeral nature", "translation": "ulotna natura", "frequency": "COMMON"}, {"phrase": "ephemeral beauty", "translation": "ulotne piękno", "frequency": "COMMON"}, {"phrase": "ephemeral trend", "translation": "przelotna moda", "frequency": "OCCASIONAL"}]'::jsonb,
    pronunciation = '{"ipa": "ɪˈfemərəl", "syllables": "e-phem-er-al", "stress": 2}'::jsonb,
    grammar = '{"comparativeForm": "more ephemeral", "superlativeForm": "most ephemeral"}'::jsonb,
    cultural_notes = 'From Greek *ephemeros* — "lasting a day". In tech, *ephemeral* data or messages are deleted automatically.',
    learning_tips = 'Link it to *efemeryda* — something that appears for a moment and is gone.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '93e418c8-b601-4117-a0d8-83c08d59a0ca'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- transient
UPDATE public.words
SET translation = 'przejściowy, przemijający',
    definition = 'Lasting only for a short time; temporary.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '2fb8b299-0199-4e9d-a720-5b44fba977a7'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing a temporary problem or symptom", "Talking about people who stay somewhere only briefly", "Formal or technical writing"]'::jsonb,
    synonyms = '["temporary", "short-lived", "passing"]'::jsonb,
    antonyms = '["permanent", "lasting", "persistent"]'::jsonb,
    common_mistakes = '["Translating *transient* as *przejrzysty* — that means *transparent*; *transient* means *przejściowy*.", "Using *transient* in casual speech where *temporary* is more natural."]'::jsonb,
    example_sentences = '[{"context": "health", "sentence": "The side effects are usually *transient*.", "translation": "Skutki uboczne są zwykle *przejściowe*."}, {"context": "technology", "sentence": "It''s a *transient* problem — the server will be back soon.", "translation": "To *przejściowy* problem — serwer zaraz wróci."}, {"context": "society", "sentence": "The town has a large *transient* population of students.", "translation": "Miasto ma dużą *napływową* populację studentów."}]'::jsonb,
    collocations = '[{"phrase": "transient symptoms", "translation": "przejściowe objawy", "frequency": "COMMON"}, {"phrase": "transient population", "translation": "ludność napływowa", "frequency": "OCCASIONAL"}, {"phrase": "transient error", "translation": "błąd przejściowy", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈtrænziənt", "syllables": "tran-si-ent", "stress": 1}'::jsonb,
    grammar = '{"comparativeForm": "more transient", "superlativeForm": "most transient"}'::jsonb,
    cultural_notes = 'In IT and engineering *transient* errors are temporary failures that usually fix themselves when you try again.',
    learning_tips = 'Think of *transit* — something just passing through.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '2fb8b299-0199-4e9d-a720-5b44fba977a7'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- fleeting
UPDATE public.words
SET translation = 'ulotny, przelotny',
    definition = 'Lasting for a very short time and quickly gone.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '3acf17a4-770f-4d99-928b-4cb113f94355'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing a quick look, smile or feeling", "Talking about memories and special moments", "Writing in a romantic or nostalgic style"]'::jsonb,
    synonyms = '["brief", "momentary", "ephemeral"]'::jsonb,
    antonyms = '["lasting", "enduring", "permanent"]'::jsonb,
    common_mistakes = '["Putting *fleeting* after the noun (*a glance fleeting*) — it almost always comes before it: *a fleeting glance*.", "Using *fleeting* for long periods — it is about moments, not months."]'::jsonb,
    example_sentences = '[{"context": "storytelling", "sentence": "I caught a *fleeting* glimpse of her in the crowd.", "translation": "*Przelotnie* mignęła mi w tłumie."}, {"context": "reflection", "sentence": "Happiness can be *fleeting*, so enjoy it.", "translation": "Szczęście bywa *ulotne*, więc się nim ciesz."}, {"context": "sport", "sentence": "For a *fleeting* moment, I thought we''d won.", "translation": "Przez *krótką* chwilę myślałem, że wygraliśmy."}]'::jsonb,
    collocations = '[{"phrase": "a fleeting glimpse", "translation": "przelotne spojrzenie, mignięcie", "frequency": "VERY_COMMON"}, {"phrase": "a fleeting moment", "translation": "krótka chwila", "frequency": "VERY_COMMON"}, {"phrase": "fleeting smile", "translation": "przelotny uśmiech", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈfliːtɪŋ", "syllables": "fleet-ing", "stress": 1}'::jsonb,
    grammar = NULL,
    cultural_notes = '*Fleeting* is very common in descriptions of feelings and moments in novels, songs and reviews.',
    learning_tips = '*To fleet* is an old verb meaning to move fast — a *fleeting* moment flies past.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '3acf17a4-770f-4d99-928b-4cb113f94355'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- deplete
UPDATE public.words
SET source_word = 'deplete',
    translation = 'wyczerpywać, uszczuplać',
    definition = 'To use up a large part of a supply of something, such as money, energy or natural resources.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'd92400eb-077d-4d7f-a095-9b850c44106f'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Discussing the environment and natural resources", "Talking about savings or budgets", "Describing physical exhaustion"]'::jsonb,
    synonyms = '["use up", "drain", "exhaust"]'::jsonb,
    antonyms = '["replenish", "restore", "top up"]'::jsonb,
    common_mistakes = '["Using *deplete* without an object (*our savings depleted*) — in active voice it needs an object: *the trip depleted our savings*, or use passive: *our savings were depleted*.", "Using *deplete* for something that is completely gone in casual speech — people usually say *run out of*."]'::jsonb,
    example_sentences = '[{"context": "environment", "sentence": "Overfishing has *depleted* fish stocks in the area.", "translation": "Nadmierne połowy *uszczupliły* zasoby ryb w tym rejonie."}, {"context": "money", "sentence": "The renovation *depleted* our savings.", "translation": "Remont *wyczerpał* nasze oszczędności."}, {"context": "work", "sentence": "Long meetings *deplete* my energy by Friday.", "translation": "Długie spotkania do piątku *wysysają* ze mnie całą energię."}]'::jsonb,
    collocations = '[{"phrase": "deplete resources", "translation": "wyczerpywać zasoby", "frequency": "VERY_COMMON"}, {"phrase": "depleted savings", "translation": "uszczuplone oszczędności", "frequency": "COMMON"}, {"phrase": "ozone depletion", "translation": "zubożenie warstwy ozonowej", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "dɪˈpliːt", "syllables": "de-plete", "stress": 2}'::jsonb,
    grammar = NULL,
    cultural_notes = 'Very frequent in environmental topics: *ozone depletion*, *depleted fish stocks*, *resource depletion*.',
    learning_tips = 'Think of a *plate* being emptied — *de-plete* takes things away until little is left.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'd92400eb-077d-4d7f-a095-9b850c44106f'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- shoot someone down in flames
UPDATE public.words
SET source_word = 'shoot someone down in flames',
    translation = 'zmiażdżyć kogoś krytyką, nie zostawić suchej nitki',
    definition = 'To criticise someone or their idea so harshly that it is completely rejected.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '1ed73d97-9755-4ef6-bfc5-39de627706b6'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing a tough meeting or presentation", "Warning someone before they share a risky idea", "Talking about harsh reviews or feedback"]'::jsonb,
    synonyms = '["tear apart", "rip to shreds", "slam"]'::jsonb,
    antonyms = '["praise", "back", "support"]'::jsonb,
    common_mistakes = '["Taking it literally — it is about criticism, not shooting anything.", "Forgetting the object — you shoot *someone* or *an idea* down: *They shot my proposal down in flames*."]'::jsonb,
    example_sentences = '[{"context": "work", "sentence": "I suggested a four-day week and my boss *shot me down in flames*.", "translation": "Zaproponowałem czterodniowy tydzień pracy, a szef *nie zostawił na mnie suchej nitki*."}, {"context": "feedback", "sentence": "Don''t *shoot them down in flames* — they''re new and still learning.", "translation": "Nie *miażdż ich krytyką* — są nowi i dopiero się uczą."}, {"context": "reviews", "sentence": "The critics *shot the film down in flames*.", "translation": "Krytycy *nie zostawili na filmie suchej nitki*."}]'::jsonb,
    collocations = '[{"phrase": "shoot an idea down", "translation": "storpedować pomysł", "frequency": "VERY_COMMON"}, {"phrase": "get shot down", "translation": "zostać zgaszonym, odrzuconym", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'The image comes from air combat, where a plane is shot down and falls in flames — the phrase is used informally in offices and reviews.',
    learning_tips = 'The shorter *shoot down* (an idea, a proposal) is even more common in everyday speech.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '1ed73d97-9755-4ef6-bfc5-39de627706b6'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- food for thought
UPDATE public.words
SET source_word = 'food for thought',
    translation = 'temat do przemyśleń, materiał do refleksji',
    definition = 'Something that makes you think carefully about a topic.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'd32becba-9815-47e6-b18a-0a5b2c9b7909'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Reacting to an interesting talk, article or conversation", "Ending a presentation or discussion", "Giving polite feedback about an idea"]'::jsonb,
    synonyms = '["something to think about"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Saying *a food for thought* — it is uncountable, so no article: *That gave me food for thought*.", "Saying *fruit of thought* — that is not an English idiom; the expression is *food for thought*."]'::jsonb,
    example_sentences = '[{"context": "work", "sentence": "Your presentation gave us a lot of *food for thought*.", "translation": "Twoja prezentacja dała nam wiele *do myślenia*."}, {"context": "media", "sentence": "That documentary was real *food for thought*.", "translation": "Ten dokument był prawdziwym *materiałem do przemyśleń*."}, {"context": "conversation", "sentence": "I don''t agree, but it''s *food for thought*.", "translation": "Nie zgadzam się, ale to *daje do myślenia*."}]'::jsonb,
    collocations = '[{"phrase": "give someone food for thought", "translation": "dać komuś do myślenia", "frequency": "VERY_COMMON"}, {"phrase": "plenty of food for thought", "translation": "mnóstwo do przemyślenia", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'A friendly, common way to show you found something interesting even if you are not fully convinced.',
    learning_tips = 'Polish *daje do myślenia* is the closest natural match — reach for it first when translating.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'd32becba-9815-47e6-b18a-0a5b2c9b7909'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- down the rabbit hole
UPDATE public.words
SET translation = 'wciągnąć się bez reszty, przepaść w temacie',
    definition = 'Becoming so absorbed in a topic, often online, that you keep going deeper and lose track of time.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '17d9c6e7-adfd-4a64-a1e2-eff9c9adebed'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Talking about browsing the internet or watching videos", "Describing a research session that went on too long", "Chatting about hobbies or obsessions"]'::jsonb,
    synonyms = '["get sucked in", "lose track of time"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Translating it literally as *w dół króliczej nory* — in Polish say *wciągnąłem się* or *przepadłem*.", "Dropping the verb — the phrase is usually *go down the rabbit hole* or *fall down the rabbit hole*."]'::jsonb,
    example_sentences = '[{"context": "internet", "sentence": "I went *down the rabbit hole* reading about ancient Rome.", "translation": "*Przepadłem* na długie godziny, czytając o starożytnym Rzymie."}, {"context": "social media", "sentence": "One video sent me *down the rabbit hole* until 3 a.m.", "translation": "Jeden filmik *wciągnął mnie* do trzeciej w nocy."}, {"context": "hobbies", "sentence": "Be careful, it''s easy to go *down the rabbit hole* with this hobby.", "translation": "Uważaj, przy tym hobby łatwo *wsiąknąć bez reszty*."}]'::jsonb,
    collocations = '[{"phrase": "go down the rabbit hole", "translation": "wciągnąć się bez reszty", "frequency": "VERY_COMMON"}, {"phrase": "fall down a rabbit hole", "translation": "przepaść w czymś", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'From *Alice in Wonderland*, where Alice follows the White Rabbit into a strange world. Today it is mostly used about the internet.',
    learning_tips = 'Picture yourself clicking link after link — that''s the rabbit hole.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '17d9c6e7-adfd-4a64-a1e2-eff9c9adebed'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- well-to-do
UPDATE public.words
SET translation = 'zamożny, dobrze sytuowany',
    definition = 'Having enough money to live comfortably; fairly rich.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'f3b391f5-91d0-4a5f-b8da-db906c812f3d'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing someone''s family background", "Talking about neighbourhoods and housing", "Polite conversation about money"]'::jsonb,
    synonyms = '["wealthy", "affluent", "well-off"]'::jsonb,
    antonyms = '["poor", "hard-up", "badly off"]'::jsonb,
    common_mistakes = '["Dropping the hyphens before a noun — write *a well-to-do family*.", "Treating it as very rich — it means comfortable, not extremely wealthy."]'::jsonb,
    example_sentences = '[{"context": "background", "sentence": "She comes from a *well-to-do* family.", "translation": "Pochodzi z *zamożnej* rodziny."}, {"context": "housing", "sentence": "It''s a quiet, *well-to-do* neighbourhood.", "translation": "To spokojna, *dobrze sytuowana* dzielnica."}, {"context": "travel", "sentence": "The resort attracts *well-to-do* tourists.", "translation": "Kurort przyciąga *zamożnych* turystów."}]'::jsonb,
    collocations = '[{"phrase": "well-to-do family", "translation": "zamożna rodzina", "frequency": "VERY_COMMON"}, {"phrase": "well-to-do area", "translation": "zamożna okolica", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˌwel tə ˈduː", "syllables": "well-to-do", "stress": 3}'::jsonb,
    grammar = NULL,
    cultural_notes = 'A polite, slightly old-fashioned way to say someone has money without sounding blunt.',
    learning_tips = 'Synonym pair to remember: *well-to-do* ≈ *well-off*.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'f3b391f5-91d0-4a5f-b8da-db906c812f3d'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- across-the-board
UPDATE public.words
SET translation = 'powszechny, obejmujący wszystkich',
    definition = 'Affecting everyone or everything in a group equally.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '4cea91f7-7ed7-4722-8044-280573fb8ade'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Business news about pay or price changes", "Company announcements about cuts or raises", "Discussing policies that apply to everyone"]'::jsonb,
    synonyms = '["general", "universal", "blanket"]'::jsonb,
    antonyms = '["selective", "targeted"]'::jsonb,
    common_mistakes = '["Hyphenating it after a verb — before a noun: *across-the-board cuts*; after a verb: *prices rose across the board* (no hyphens).", "Using it for small groups — it implies a whole organisation, sector or set of items."]'::jsonb,
    example_sentences = '[{"context": "work", "sentence": "The company announced *across-the-board* pay rises.", "translation": "Firma ogłosiła *powszechne* podwyżki."}, {"context": "economy", "sentence": "Prices went up *across the board*.", "translation": "Ceny wzrosły *we wszystkich kategoriach*."}, {"context": "results", "sentence": "We saw improvements *across the board*.", "translation": "Widzieliśmy poprawę *na całej linii*."}]'::jsonb,
    collocations = '[{"phrase": "across-the-board cuts", "translation": "cięcia we wszystkich obszarach", "frequency": "VERY_COMMON"}, {"phrase": "across-the-board increase", "translation": "ogólna podwyżka", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'Originally from horse-race betting: a bet *across the board* covers win, place and show.',
    learning_tips = 'Polish *na całej linii* is a natural match when the phrase comes after a verb.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '4cea91f7-7ed7-4722-8044-280573fb8ade'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- run-of-the-mill
UPDATE public.words
SET translation = 'przeciętny, zwyczajny',
    definition = 'Ordinary and not special in any way.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'b1898c39-791a-48f3-ab3a-3ef7e03d8fad'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Reviewing films, restaurants or products", "Describing a routine day or task", "Comparing something special with something average"]'::jsonb,
    synonyms = '["ordinary", "average", "unremarkable"]'::jsonb,
    antonyms = '["exceptional", "outstanding", "unusual"]'::jsonb,
    common_mistakes = '["Using it as praise — it is mildly negative: it means *nothing special*.", "Forgetting the hyphens before a noun: *a run-of-the-mill hotel*."]'::jsonb,
    example_sentences = '[{"context": "review", "sentence": "It was just a *run-of-the-mill* action film.", "translation": "To był zupełnie *przeciętny* film akcji."}, {"context": "work", "sentence": "This isn''t a *run-of-the-mill* job interview.", "translation": "To nie jest *zwykła* rozmowa o pracę."}, {"context": "travel", "sentence": "The hotel was clean but pretty *run-of-the-mill*.", "translation": "Hotel był czysty, ale dość *przeciętny*."}]'::jsonb,
    collocations = '[{"phrase": "a run-of-the-mill job", "translation": "zwykła praca", "frequency": "COMMON"}, {"phrase": "run-of-the-mill product", "translation": "przeciętny produkt", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'From factories: goods that *ran off the mill* without being sorted or improved.',
    learning_tips = 'Use it when you''d say *nic specjalnego* in Polish.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'b1898c39-791a-48f3-ab3a-3ef7e03d8fad'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- state-of-the-art
UPDATE public.words
SET definition = 'Using the newest and most advanced technology or methods available.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '05afa286-5935-4cd4-a9a1-a51038149eb3'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing equipment, buildings or technology", "Marketing and product descriptions", "Job interviews about a company''s tools"]'::jsonb,
    synonyms = '["cutting-edge", "advanced", "modern"]'::jsonb,
    antonyms = '["outdated", "obsolete", "old-fashioned"]'::jsonb,
    common_mistakes = '["Dropping the hyphens before a noun — write *state-of-the-art equipment*.", "Overusing it in casual speech — it sounds like marketing; friends might just say *really modern*."]'::jsonb,
    example_sentences = '[{"context": "health", "sentence": "The hospital has *state-of-the-art* equipment.", "translation": "Szpital ma *najnowocześniejszy* sprzęt."}, {"context": "work", "sentence": "They work in a *state-of-the-art* office in the city centre.", "translation": "Pracują w *supernowoczesnym* biurze w centrum."}, {"context": "sport", "sentence": "The stadium''s sound system is *state-of-the-art*.", "translation": "Nagłośnienie stadionu to *najwyższa półka*."}]'::jsonb,
    collocations = '[{"phrase": "state-of-the-art technology", "translation": "najnowocześniejsza technologia", "frequency": "VERY_COMMON"}, {"phrase": "state-of-the-art facility", "translation": "nowoczesny obiekt", "frequency": "VERY_COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'Very common in adverts, press releases and job offers.',
    learning_tips = 'Synonym worth learning together: *cutting-edge*.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '05afa286-5935-4cd4-a9a1-a51038149eb3'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- off-the-cuff
UPDATE public.words
SET translation = 'spontaniczny, z marszu',
    definition = 'Said or done without preparation.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '82f7cb46-3e87-433a-a4d5-c5fa2eaf7eff'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Talking about speeches, comments or answers", "Meetings where you''re asked for a quick estimate", "Describing someone''s sense of humour"]'::jsonb,
    synonyms = '["spontaneous", "improvised", "impromptu"]'::jsonb,
    antonyms = '["prepared", "rehearsed", "scripted"]'::jsonb,
    common_mistakes = '["Using hyphens after a verb — *an off-the-cuff remark*, but *he spoke off the cuff*.", "Thinking it means rude — it only means unprepared."]'::jsonb,
    example_sentences = '[{"context": "conversation", "sentence": "It was just an *off-the-cuff* remark — don''t take it seriously.", "translation": "To była tylko *rzucona mimochodem* uwaga — nie bierz jej na serio."}, {"context": "work", "sentence": "I can''t give you exact numbers *off the cuff*.", "translation": "Nie podam ci dokładnych liczb *z głowy*."}, {"context": "event", "sentence": "Her *off-the-cuff* speech was the best of the evening.", "translation": "Jej *spontaniczne* przemówienie było najlepsze tego wieczoru."}]'::jsonb,
    collocations = '[{"phrase": "off-the-cuff remark", "translation": "uwaga rzucona mimochodem", "frequency": "VERY_COMMON"}, {"phrase": "speak off the cuff", "translation": "mówić bez przygotowania", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'Comes from speakers writing quick notes on their shirt cuffs instead of preparing a full speech.',
    learning_tips = 'Polish equivalents to keep in mind: *z marszu*, *na gorąco*, *z głowy*.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '82f7cb46-3e87-433a-a4d5-c5fa2eaf7eff'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- larger-than-life
UPDATE public.words
SET type = 'ADJECTIVE',
    translation = 'barwny, charyzmatyczny',
    definition = 'Very noticeable, exciting and impressive, especially about a person''s personality.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'a9be3be1-4d41-43a1-b8ee-be4d0cb41417'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing a colourful person or character", "Talking about celebrities or historical figures", "Book and film reviews"]'::jsonb,
    synonyms = '["flamboyant", "charismatic", "colourful"]'::jsonb,
    antonyms = '["dull", "unremarkable", "low-key"]'::jsonb,
    common_mistakes = '["Translating it literally as *większy niż życie* — in Polish say *barwna postać* or *charyzmatyczny*.", "Using it for physical size — it describes personality or presence, not height."]'::jsonb,
    example_sentences = '[{"context": "family", "sentence": "My uncle was a *larger-than-life* character.", "translation": "Mój wujek był niezwykle *barwną* postacią."}, {"context": "review", "sentence": "The film celebrates a *larger-than-life* rock star.", "translation": "Film opowiada o *charyzmatycznej* gwieździe rocka."}, {"context": "describing people", "sentence": "She has a *larger-than-life* personality.", "translation": "Ma bardzo *wyrazistą* osobowość."}]'::jsonb,
    collocations = '[{"phrase": "larger-than-life character", "translation": "barwna postać", "frequency": "VERY_COMMON"}, {"phrase": "larger-than-life personality", "translation": "wyrazista osobowość", "frequency": "VERY_COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'Usually positive or affectionate, often used about performers, leaders and memorable relatives.',
    learning_tips = 'Picture someone who fills the whole room with their presence.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'a9be3be1-4d41-43a1-b8ee-be4d0cb41417'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- middle-of-the-road
UPDATE public.words
SET translation = 'umiarkowany, zachowawczy',
    definition = 'Not extreme; moderate, sometimes in a way that feels safe or unexciting.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'd4673027-c4f5-4ace-8caa-64ca1ea560bb'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Discussing politics and opinions", "Reviewing music, films or products", "Talking about cautious decisions"]'::jsonb,
    synonyms = '["moderate", "mainstream", "centrist"]'::jsonb,
    antonyms = '["radical", "extreme", "bold"]'::jsonb,
    common_mistakes = '["Assuming it is always positive — for music or art it often means bland.", "Hyphenation: *a middle-of-the-road policy*, but *he stays in the middle of the road*."]'::jsonb,
    example_sentences = '[{"context": "politics", "sentence": "The party is trying to win *middle-of-the-road* voters.", "translation": "Partia próbuje zdobyć *umiarkowanych* wyborców."}, {"context": "music", "sentence": "Their new album is pleasant but very *middle-of-the-road*.", "translation": "Ich nowy album jest przyjemny, ale bardzo *zachowawczy*."}, {"context": "decisions", "sentence": "We chose a *middle-of-the-road* option to keep everyone happy.", "translation": "Wybraliśmy *kompromisowe* rozwiązanie, żeby wszyscy byli zadowoleni."}]'::jsonb,
    collocations = '[{"phrase": "middle-of-the-road voters", "translation": "umiarkowani wyborcy", "frequency": "COMMON"}, {"phrase": "middle-of-the-road approach", "translation": "umiarkowane podejście", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'Common in British and American political commentary; in music reviews it often appears as *MOR*.',
    learning_tips = 'The middle of the road is the safe place, far from both edges.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'd4673027-c4f5-4ace-8caa-64ca1ea560bb'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- thought-out
UPDATE public.words
SET source_word = 'thought-out',
    type = 'ADJECTIVE',
    translation = 'przemyślany',
    definition = 'Planned or considered carefully; usually used as well-thought-out or poorly thought-out.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'a331d7b8-9da4-46fd-a3ed-47fb3e62927d'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Giving feedback on a plan or proposal", "Describing a decision or argument", "Reviewing a product''s design"]'::jsonb,
    synonyms = '["considered", "well-planned"]'::jsonb,
    antonyms = '["hasty", "half-baked", "ill-considered"]'::jsonb,
    common_mistakes = '["Confusing it with *though* or *throughout* — the root is *think → thought*.", "Using it alone — it is almost always combined with *well-*, *carefully* or *poorly*."]'::jsonb,
    example_sentences = '[{"context": "work", "sentence": "It''s a *well-thought-out* plan.", "translation": "To dobrze *przemyślany* plan."}, {"context": "review", "sentence": "The ending of the film felt poorly *thought-out*.", "translation": "Zakończenie filmu wydawało się słabo *przemyślane*."}, {"context": "conversation", "sentence": "She gave a carefully *thought-out* answer.", "translation": "Udzieliła starannie *przemyślanej* odpowiedzi."}]'::jsonb,
    collocations = '[{"phrase": "well-thought-out plan", "translation": "dobrze przemyślany plan", "frequency": "VERY_COMMON"}, {"phrase": "poorly thought-out", "translation": "słabo przemyślany", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˌθɔːt ˈaʊt", "syllables": "thought-out", "stress": 2}'::jsonb,
    grammar = NULL,
    cultural_notes = 'A common phrase in feedback at work and school.',
    learning_tips = 'It comes from the phrasal verb *think something out* — to plan it carefully.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'a331d7b8-9da4-46fd-a3ed-47fb3e62927d'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- paid-for
UPDATE public.words
SET translation = 'opłacony',
    definition = 'Already paid for, so nothing more is owed.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '530b52d4-006e-4af5-ad2d-0cb656a4bbfc'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Talking about subscriptions and services", "Business travel and expenses", "Discussing ownership of a house or car"]'::jsonb,
    synonyms = '["prepaid", "settled"]'::jsonb,
    antonyms = '["unpaid", "free"]'::jsonb,
    common_mistakes = '["Translating it as *płatny* — *płatny* means *you must pay*; *paid-for* means *already paid*: *opłacony*.", "Hyphenation: *a paid-for trip*, but *the trip is paid for*."]'::jsonb,
    example_sentences = '[{"context": "travel", "sentence": "It''s a fully *paid-for* trip, so you don''t need any money.", "translation": "To w pełni *opłacona* wycieczka, więc nie potrzebujesz pieniędzy."}, {"context": "finance", "sentence": "The house is *paid for* — we have no mortgage.", "translation": "Dom jest *spłacony* — nie mamy kredytu."}, {"context": "software", "sentence": "Is this a *paid-for* feature or a free one?", "translation": "To funkcja *płatna* czy darmowa?"}]'::jsonb,
    collocations = '[{"phrase": "paid-for subscription", "translation": "opłacona subskrypcja", "frequency": "COMMON"}, {"phrase": "fully paid for", "translation": "w pełni opłacony", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'Often used in business to separate paid services from free trials.',
    learning_tips = 'Paid + for — the payment has already happened.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '530b52d4-006e-4af5-ad2d-0cb656a4bbfc'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- take-it-or-leave-it
UPDATE public.words
SET translation = 'ostateczny, bez negocjacji',
    definition = 'Describes an offer that will not change: you either accept it or reject it.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '80256eeb-7765-4c23-9fce-9ee67096670a'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Negotiating a price or salary", "Describing a tough deal", "Talking about someone''s attitude"]'::jsonb,
    synonyms = '["final", "non-negotiable"]'::jsonb,
    antonyms = '["negotiable", "flexible"]'::jsonb,
    common_mistakes = '["Saying *take it or let it* — the fixed phrase is *take it or leave it*.", "Hyphenation: *a take-it-or-leave-it offer*, but as a sentence: *Take it or leave it!*"]'::jsonb,
    example_sentences = '[{"context": "business", "sentence": "They made us a *take-it-or-leave-it* offer.", "translation": "Złożyli nam *ostateczną* ofertę, bez negocjacji."}, {"context": "market", "sentence": "\"Fifty euros. *Take it or leave it*.\"", "translation": "„Pięćdziesiąt euro. *Bierzesz albo nie*”."}, {"context": "attitude", "sentence": "I don''t like his *take-it-or-leave-it* attitude.", "translation": "Nie podoba mi się jego podejście *„bierzesz albo nie”*."}]'::jsonb,
    collocations = '[{"phrase": "take-it-or-leave-it offer", "translation": "ostateczna oferta", "frequency": "VERY_COMMON"}, {"phrase": "take-it-or-leave-it deal", "translation": "umowa bez negocjacji", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'Common in negotiations; using it can sound tough or even rude.',
    learning_tips = 'Polish *bierzesz albo nie* covers the spoken version nicely.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '80256eeb-7765-4c23-9fce-9ee67096670a'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- go through
UPDATE public.words
SET translation = 'przejrzeć, przejść przez coś',
    definition = 'To examine something carefully, or to experience something difficult.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '57679f5b-de4c-4730-8af9-71d42cfdd015'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Reviewing documents, emails or notes", "Talking about a hard period in life", "Checking whether a payment or deal was accepted"]'::jsonb,
    synonyms = '["review", "examine", "experience"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Saying *go through with* when you mean *examine* — *go through with* means *carry out something you planned*.", "Forgetting the irregular forms: *went through*, *gone through*."]'::jsonb,
    example_sentences = '[{"context": "work", "sentence": "Let''s *go through* the contract together.", "translation": "*Przejrzyjmy* razem umowę."}, {"context": "life", "sentence": "She''s *going through* a difficult time.", "translation": "*Przechodzi* teraz trudny okres."}, {"context": "shopping", "sentence": "Did the payment *go through*?", "translation": "Czy płatność *przeszła*?"}]'::jsonb,
    collocations = '[{"phrase": "go through the details", "translation": "omówić szczegóły", "frequency": "VERY_COMMON"}, {"phrase": "go through a hard time", "translation": "przechodzić trudny okres", "frequency": "VERY_COMMON"}, {"phrase": "the payment went through", "translation": "płatność przeszła", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ɡəʊ ˈθruː", "syllables": "go through", "stress": 2}'::jsonb,
    grammar = '{"irregularForms": {"pastTense": "went through", "pastParticiple": "gone through", "presentParticiple": "going through"}}'::jsonb,
    cultural_notes = 'One of the most useful phrasal verbs in everyday English — the meaning depends on context.',
    learning_tips = 'Learn it with three objects: *go through papers*, *go through a divorce*, *the payment went through*.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '57679f5b-de4c-4730-8af9-71d42cfdd015'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- business as usual
UPDATE public.words
SET source_word = 'business as usual',
    translation = 'normalne funkcjonowanie',
    definition = 'The normal way things are done, continuing even when something unusual has happened.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '9027edba-d197-4968-acda-ea6627d2b440'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Company updates during a crisis or change", "Reassuring customers that nothing changes", "Criticising a lack of change"]'::jsonb,
    synonyms = '["status quo", "normal operations"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Hyphenating it after a verb — *it''s business as usual*; hyphens only before a noun: *a business-as-usual approach*.", "Thinking it is always positive — it can criticise a refusal to change."]'::jsonb,
    example_sentences = '[{"context": "crisis", "sentence": "Despite the storm, it''s *business as usual* at the shop.", "translation": "Mimo burzy sklep *działa normalnie*."}, {"context": "business", "sentence": "After the merger, it will be *business as usual* for customers.", "translation": "Po fuzji dla klientów *nic się nie zmieni*."}, {"context": "criticism", "sentence": "We can''t go back to *business as usual* after this.", "translation": "Po czymś takim nie możemy wrócić *do starego trybu*."}]'::jsonb,
    collocations = '[{"phrase": "it''s business as usual", "translation": "wszystko działa normalnie", "frequency": "VERY_COMMON"}, {"phrase": "return to business as usual", "translation": "powrót do normalności", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'Frequent in company statements; in politics it is often used critically, e.g. about climate policy.',
    learning_tips = 'Polish *wszystko po staremu* captures the spoken meaning.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '9027edba-d197-4968-acda-ea6627d2b440'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- all-you-can-eat
UPDATE public.words
SET translation = 'bez limitu, do woli',
    definition = 'A meal or restaurant where you pay one price and can eat as much as you want.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '1bc487fd-8aae-4ffa-8cd8-caeac700068a'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Choosing a restaurant", "Talking about buffets on holiday", "Restaurant adverts and menus"]'::jsonb,
    synonyms = '["unlimited", "buffet-style"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Translating it word for word as *wszystko, co możesz zjeść* — say *bufet bez limitu* or *jedzenie do woli*.", "Using it after the noun — it goes before: *an all-you-can-eat buffet*."]'::jsonb,
    example_sentences = '[{"context": "going out", "sentence": "Let''s try the *all-you-can-eat* sushi place.", "translation": "Chodźmy do tej knajpy z sushi *bez limitu*."}, {"context": "travel", "sentence": "The hotel has an *all-you-can-eat* breakfast buffet.", "translation": "Hotel ma śniadanie w formie bufetu *do woli*."}, {"context": "everyday", "sentence": "I always eat too much at *all-you-can-eat* restaurants.", "translation": "W restauracjach *bez limitu* zawsze przesadzam z jedzeniem."}]'::jsonb,
    collocations = '[{"phrase": "all-you-can-eat buffet", "translation": "bufet bez limitu", "frequency": "VERY_COMMON"}, {"phrase": "all-you-can-eat deal", "translation": "oferta jedzenia do woli", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'Popular in American and Asian restaurant culture, especially for sushi, pizza and barbecue.',
    learning_tips = 'Remember it as a whole label you''d see on a restaurant window.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '1bc487fd-8aae-4ffa-8cd8-caeac700068a'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- meagre
UPDATE public.words
SET type = 'ADJECTIVE',
    translation = 'skromny, mizerny',
    definition = 'Very small or not enough in amount or quality.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '28f8f190-046a-413b-9706-efab42539f0b'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Talking about low pay or income", "Describing small food portions", "Commenting on weak results"]'::jsonb,
    synonyms = '["scanty", "paltry", "modest"]'::jsonb,
    antonyms = '["generous", "ample", "plentiful"]'::jsonb,
    common_mistakes = '["Using *meagre* to describe a thin person — for people say *thin* or *skinny*; *meagre* is for amounts (*a meagre salary*).", "Treating *meager* as a typo — it is simply the American spelling of *meagre*; pick one and use it consistently.", "Using it for small objects — it describes amounts and quantities, not size."]'::jsonb,
    example_sentences = '[{"context": "money", "sentence": "They live on a *meagre* income.", "translation": "Utrzymują się ze *skromnych* dochodów."}, {"context": "restaurant", "sentence": "The portions were pretty *meagre* for the price.", "translation": "Porcje były dość *mizerne* jak na tę cenę."}, {"context": "work", "sentence": "Our efforts produced *meagre* results.", "translation": "Nasze starania przyniosły *mizerne* rezultaty."}]'::jsonb,
    collocations = '[{"phrase": "meagre salary", "translation": "marna pensja", "frequency": "VERY_COMMON"}, {"phrase": "meagre portions", "translation": "skromne porcje", "frequency": "COMMON"}, {"phrase": "meagre results", "translation": "mizerne wyniki", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈmiːɡər", "syllables": "mea-gre", "stress": 1}'::jsonb,
    grammar = '{"comparativeForm": "meagrer", "superlativeForm": "meagrest"}'::jsonb,
    cultural_notes = 'Often used with money and food — and almost always with a disappointed tone.',
    learning_tips = 'Don''t confuse it with *merge* (łączyć) — they only look similar.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '28f8f190-046a-413b-9706-efab42539f0b'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- firm
UPDATE public.words
SET definition = 'A company, especially one that offers professional services such as law or accounting.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'c659818a-ca32-40e4-aac9-33386432027a'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Talking about where you work", "Business news and job offers", "Professional services like law or consulting"]'::jsonb,
    synonyms = '["company", "business", "practice"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Using *firm* for any company — it typically refers to professional services (*law firm*, *accounting firm*), less often to factories or shops.", "Confusing the noun with the adjective *firm* (stanowczy, twardy) — context tells you which one is meant."]'::jsonb,
    example_sentences = '[{"context": "work", "sentence": "He works for a law *firm* in London.", "translation": "Pracuje w *kancelarii* prawnej w Londynie."}, {"context": "business", "sentence": "She started her own consulting *firm*.", "translation": "Założyła własną *firmę* doradczą."}, {"context": "business news", "sentence": "The *firm* employs over two hundred people.", "translation": "*Firma* zatrudnia ponad dwieście osób."}]'::jsonb,
    collocations = '[{"phrase": "law firm", "translation": "kancelaria prawna", "frequency": "VERY_COMMON"}, {"phrase": "accounting firm", "translation": "biuro rachunkowe", "frequency": "COMMON"}, {"phrase": "consulting firm", "translation": "firma doradcza", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "fɜːrm", "syllables": "firm", "stress": 1}'::jsonb,
    grammar = '{"pluralForm": "firms"}'::jsonb,
    cultural_notes = 'In English-speaking countries the big accounting and law companies are often called *the big firms*.',
    learning_tips = 'Notice that *law firm* is best translated as *kancelaria*, not *firma prawnicza*.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'c659818a-ca32-40e4-aac9-33386432027a'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- doting
UPDATE public.words
SET translation = 'czuły, bezgranicznie kochający',
    definition = 'Showing a lot of love for someone, sometimes too much, especially as a parent or grandparent.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'fc4aa25a-7c25-4236-b9bc-c821cb32105d'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing parents and grandparents", "Talking about a loving partner", "Family stories"]'::jsonb,
    synonyms = '["adoring", "devoted", "loving"]'::jsonb,
    antonyms = '["cold", "indifferent", "neglectful"]'::jsonb,
    common_mistakes = '["Using *doting* for objects or activities — it describes people who love other people (or pets).", "Forgetting the verb form *dote on*: *She dotes on her grandchildren*."]'::jsonb,
    example_sentences = '[{"context": "family", "sentence": "He has a *doting* grandmother who spoils him.", "translation": "Ma *zakochaną w nim po uszy* babcię, która go rozpieszcza."}, {"context": "family", "sentence": "She''s a *doting* mother of three.", "translation": "Jest *czułą* matką trójki dzieci."}, {"context": "relationships", "sentence": "Her *doting* husband brought her flowers every week.", "translation": "Jej *zapatrzony w nią* mąż co tydzień przynosił jej kwiaty."}]'::jsonb,
    collocations = '[{"phrase": "doting parents", "translation": "kochający rodzice", "frequency": "VERY_COMMON"}, {"phrase": "doting grandmother", "translation": "czuła babcia", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈdəʊtɪŋ", "syllables": "dot-ing", "stress": 1}'::jsonb,
    grammar = NULL,
    cultural_notes = 'Usually warm and affectionate, sometimes with a hint of spoiling.',
    learning_tips = 'Learn it with *dote on someone* — to love and spoil them.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'fc4aa25a-7c25-4236-b9bc-c821cb32105d'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- bluntly
UPDATE public.words
SET type = 'ADVERB',
    translation = 'bez ogródek, prosto z mostu',
    definition = 'In a direct and honest way, without trying to be polite or gentle.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '912a72e4-6dd4-4b2e-aad4-5d5c69418042'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Giving honest feedback", "Difficult conversations at work", "Introducing an uncomfortable opinion"]'::jsonb,
    synonyms = '["frankly", "directly", "candidly"]'::jsonb,
    antonyms = '["tactfully", "diplomatically", "gently"]'::jsonb,
    common_mistakes = '["Using *bluntly* as an adjective — the adjective is *blunt*: *a blunt answer*, *he answered bluntly*.", "Saying *to put it blunt* — the fixed phrase is *to put it bluntly*."]'::jsonb,
    example_sentences = '[{"context": "work", "sentence": "To put it *bluntly*, the plan won''t work.", "translation": "Mówiąc *bez ogródek*, ten plan nie wypali."}, {"context": "conversation", "sentence": "She told him *bluntly* that he was late again.", "translation": "Powiedziała mu *prosto z mostu*, że znowu się spóźnił."}, {"context": "business", "sentence": "The report *bluntly* criticises the management.", "translation": "Raport *otwarcie* krytykuje zarząd."}]'::jsonb,
    collocations = '[{"phrase": "to put it bluntly", "translation": "mówiąc wprost", "frequency": "VERY_COMMON"}, {"phrase": "speak bluntly", "translation": "mówić bez ogródek", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈblʌntli", "syllables": "blunt-ly", "stress": 1}'::jsonb,
    grammar = NULL,
    cultural_notes = 'Being blunt can be seen as honest or rude; British speakers often soften it with *to be honest* first.',
    learning_tips = '*To put it bluntly* is a ready-made phrase — learn it as one chunk.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '912a72e4-6dd4-4b2e-aad4-5d5c69418042'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- to add insult to injury
UPDATE public.words
SET translation = 'na domiar złego',
    definition = 'Used when something makes an already bad situation even worse.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'db1c9042-bf1b-42e3-b453-f238d4590518'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Telling a story about a bad day", "Complaining about poor service", "Describing a series of misfortunes"]'::jsonb,
    synonyms = '["to make matters worse", "on top of that"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Changing the word order (*add injury to insult*) — it is a fixed phrase.", "Using it for good news — it always introduces something negative."]'::jsonb,
    example_sentences = '[{"context": "bad day", "sentence": "I missed the bus and, *to add insult to injury*, it started raining.", "translation": "Uciekł mi autobus, a *na domiar złego* zaczęło padać."}, {"context": "complaint", "sentence": "The food was cold, and *to add insult to injury*, they charged us extra.", "translation": "Jedzenie było zimne, a *na domiar złego* doliczyli nam opłatę."}, {"context": "sport", "sentence": "We lost the match, and *to add insult to injury*, our best player got injured.", "translation": "Przegraliśmy mecz, a *na domiar złego* nasz najlepszy zawodnik doznał kontuzji."}]'::jsonb,
    collocations = '[{"phrase": "and to add insult to injury", "translation": "a na domiar złego", "frequency": "VERY_COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'One of the most common English idioms for things going from bad to worse.',
    learning_tips = 'Polish *na domiar złego* is the perfect, natural equivalent.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'db1c9042-bf1b-42e3-b453-f238d4590518'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- to cap it all
UPDATE public.words
SET translation = 'na dodatek, w dodatku',
    definition = 'Used to introduce the last and often worst thing in a series of events.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'f5a378cf-dc63-4785-95a0-c7a93ee665d5'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Telling a story about a terrible trip", "Complaining about a bad week", "Humorous storytelling"]'::jsonb,
    synonyms = '["to top it all off", "on top of everything"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Saying *to cap it* without *all* — the idiom is *to cap it all* (or *to top it all*).", "Using it mid-story — it normally introduces the final event."]'::jsonb,
    example_sentences = '[{"context": "travel", "sentence": "It rained all week and, *to cap it all*, our flight was cancelled.", "translation": "Cały tydzień padało, a *na dodatek* odwołali nam lot."}, {"context": "work", "sentence": "The project was late and, *to cap it all*, the client cancelled.", "translation": "Projekt się opóźnił, a *w dodatku* klient się wycofał."}, {"context": "bad day", "sentence": "*To cap it all*, I locked my keys in the car.", "translation": "*Na dodatek* zatrzasnąłem kluczyki w samochodzie."}]'::jsonb,
    collocations = '[{"phrase": "and to cap it all", "translation": "a na dodatek", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'More common in British English; Americans tend to say *to top it all off*.',
    learning_tips = 'A *cap* goes on top — this idiom puts the final thing on top of the pile.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'f5a378cf-dc63-4785-95a0-c7a93ee665d5'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- flustered
UPDATE public.words
SET translation = 'speszony, zdenerwowany',
    definition = 'Nervous and confused, often because you are rushed or embarrassed.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '19fd0bdc-6404-4d24-aee3-aefba0687ef0'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing a stressful interview or exam", "Talking about embarrassing moments", "Stories about being in a hurry"]'::jsonb,
    synonyms = '["rattled", "agitated", "flummoxed"]'::jsonb,
    antonyms = '["calm", "composed", "relaxed"]'::jsonb,
    common_mistakes = '["Confusing it with *frustrated* — *flustered* is nervous and confused, *frustrated* is annoyed because you can''t do something.", "Using it for long-term states — it describes a short, sudden moment."]'::jsonb,
    example_sentences = '[{"context": "interview", "sentence": "She got *flustered* when they asked about her salary.", "translation": "*Speszyła się*, gdy zapytali ją o pensję."}, {"context": "embarrassment", "sentence": "He was so *flustered* that he forgot his own phone number.", "translation": "Był tak *zdenerwowany*, że zapomniał własnego numeru telefonu."}, {"context": "reassurance", "sentence": "Don''t get *flustered* — we have plenty of time.", "translation": "Nie *denerwuj się* — mamy mnóstwo czasu."}]'::jsonb,
    collocations = '[{"phrase": "get flustered", "translation": "stracić głowę, speszyć się", "frequency": "VERY_COMMON"}, {"phrase": "look flustered", "translation": "wyglądać na zdenerwowanego", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈflʌstərd", "syllables": "flus-tered", "stress": 1}'::jsonb,
    grammar = NULL,
    cultural_notes = 'Often describes visible signs like blushing, stammering or dropping things.',
    learning_tips = 'Remember the contrast: *flustered* ≠ *frustrated*.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '19fd0bdc-6404-4d24-aee3-aefba0687ef0'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- blunt
UPDATE public.words
SET translation = 'tępy, bezpośredni',
    definition = 'Not sharp; or, about a person, saying what they think directly without trying to be polite.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7ef0d8e0-a5df-43d9-8d2c-baf16cb30ab6'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing a knife or tool", "Giving or receiving honest feedback", "Describing someone''s communication style"]'::jsonb,
    synonyms = '["dull", "direct", "frank"]'::jsonb,
    antonyms = '["sharp", "tactful", "diplomatic"]'::jsonb,
    common_mistakes = '["Using *blunt* as an adverb — the adverb is *bluntly*: *she spoke bluntly*.", "Assuming it is always rude — a *blunt* answer can simply be honest."]'::jsonb,
    example_sentences = '[{"context": "kitchen", "sentence": "This knife is too *blunt* to cut bread.", "translation": "Ten nóż jest zbyt *tępy*, żeby kroić chleb."}, {"context": "feedback", "sentence": "Sorry to be *blunt*, but your idea won''t work.", "translation": "Przepraszam, że mówię *wprost*, ale twój pomysł nie wypali."}, {"context": "describing people", "sentence": "He''s very *blunt* — he says exactly what he thinks.", "translation": "Jest bardzo *bezpośredni* — mówi dokładnie to, co myśli."}]'::jsonb,
    collocations = '[{"phrase": "blunt knife", "translation": "tępy nóż", "frequency": "COMMON"}, {"phrase": "blunt honesty", "translation": "brutalna szczerość", "frequency": "COMMON"}, {"phrase": "to be blunt", "translation": "mówiąc wprost", "frequency": "VERY_COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "blʌnt", "syllables": "blunt", "stress": 1}'::jsonb,
    grammar = '{"comparativeForm": "blunter", "superlativeForm": "bluntest"}'::jsonb,
    cultural_notes = '*Sorry to be blunt, but…* is a common way to warn people that honest criticism is coming.',
    learning_tips = 'A *blunt* knife has no sharp edge; a *blunt* person has no polite "soft edge".',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '7ef0d8e0-a5df-43d9-8d2c-baf16cb30ab6'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- out of the blue
UPDATE public.words
SET translation = 'nagle, ni stąd ni zowąd',
    definition = 'Suddenly and unexpectedly.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'fb58b90a-4b7e-418c-a225-732451467c04'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Talking about unexpected calls or messages", "Describing sudden news or offers", "Telling stories"]'::jsonb,
    synonyms = '["unexpectedly", "suddenly", "out of nowhere"]'::jsonb,
    antonyms = '["as expected"]'::jsonb,
    common_mistakes = '["Dropping the article (*out of blue*) — it is always *out of THE blue*.", "Using it for things you planned — it only fits surprises."]'::jsonb,
    example_sentences = '[{"context": "personal", "sentence": "She called me *out of the blue* after five years.", "translation": "Zadzwoniła do mnie *ni stąd, ni zowąd* po pięciu latach."}, {"context": "work", "sentence": "The job offer came completely *out of the blue*.", "translation": "Oferta pracy przyszła zupełnie *niespodziewanie*."}, {"context": "storytelling", "sentence": "*Out of the blue*, he started crying.", "translation": "*Nagle* zaczął płakać."}]'::jsonb,
    collocations = '[{"phrase": "come out of the blue", "translation": "pojawić się znikąd", "frequency": "VERY_COMMON"}, {"phrase": "completely out of the blue", "translation": "zupełnie niespodziewanie", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'From *a bolt from the blue* — lightning from a clear blue sky.',
    learning_tips = 'Imagine a lightning bolt from a clear blue sky — totally unexpected.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'fb58b90a-4b7e-418c-a225-732451467c04'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- mingle
UPDATE public.words
SET translation = 'integrować się, krążyć wśród gości',
    definition = 'To move around and talk to different people at a social event.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '5c1ed01f-2e2c-4eb7-a8ea-479f21b91dc2'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Networking events and conferences", "Parties and weddings", "Describing how someone behaves socially"]'::jsonb,
    synonyms = '["socialize", "circulate", "network"]'::jsonb,
    antonyms = '["keep to yourself"]'::jsonb,
    common_mistakes = '["Translating it as *mieszać* — for people it means moving around and chatting, not *mixing* things.", "Forgetting the preposition — you mingle *with* people: *mingle with the guests*."]'::jsonb,
    example_sentences = '[{"context": "party", "sentence": "Grab a drink and *mingle* — you''ll meet lots of nice people.", "translation": "Weź coś do picia i *pokręć się wśród gości* — poznasz mnóstwo fajnych ludzi."}, {"context": "networking", "sentence": "At conferences I find it hard to *mingle* with strangers.", "translation": "Na konferencjach trudno mi *nawiązywać rozmowy* z obcymi."}, {"context": "event", "sentence": "The host *mingled* with the guests all evening.", "translation": "Gospodarz przez cały wieczór *krążył wśród gości*."}]'::jsonb,
    collocations = '[{"phrase": "mingle with guests", "translation": "rozmawiać z gośćmi", "frequency": "VERY_COMMON"}, {"phrase": "mingle at a party", "translation": "integrować się na imprezie", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈmɪŋɡl", "syllables": "min-gle", "stress": 1}'::jsonb,
    grammar = NULL,
    cultural_notes = 'At English-speaking events *mingling* is expected — staying only with people you know can seem unfriendly.',
    learning_tips = 'Polish has no single verb for it; think *krążyć wśród gości i zagadywać*.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '5c1ed01f-2e2c-4eb7-a8ea-479f21b91dc2'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- hover
UPDATE public.words
SET translation = 'unosić się, krążyć w pobliżu',
    definition = 'To stay in the air in one place; or to wait or stand near someone in a nervous or annoying way.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'cadf2f13-c0b9-46dd-b803-e4e11fdbe024'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing helicopters, drones or insects", "Talking about computer mouse actions", "Describing someone waiting around nervously"]'::jsonb,
    synonyms = '["float", "linger", "hang around"]'::jsonb,
    antonyms = '["land", "leave"]'::jsonb,
    common_mistakes = '["Using *hover* for things floating on water — that is *float*; *hover* is in the air.", "Forgetting *over/above*: *The drone hovered above the field*."]'::jsonb,
    example_sentences = '[{"context": "everyday", "sentence": "A drone was *hovering* above the house.", "translation": "Nad domem *unosił się* dron."}, {"context": "computers", "sentence": "*Hover* over the icon to see more options.", "translation": "*Najedź kursorem* na ikonę, żeby zobaczyć więcej opcji."}, {"context": "home", "sentence": "Stop *hovering* — I''ll call you when dinner''s ready.", "translation": "Przestań *kręcić się koło mnie* — zawołam cię, jak obiad będzie gotowy."}]'::jsonb,
    collocations = '[{"phrase": "hover over", "translation": "najechać na, unosić się nad", "frequency": "VERY_COMMON"}, {"phrase": "hover around", "translation": "kręcić się wokół", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈhʌvər", "syllables": "hov-er", "stress": 1}'::jsonb,
    grammar = NULL,
    cultural_notes = 'In computing, *hovering* means moving the mouse over something without clicking.',
    learning_tips = 'Think of a hummingbird or a helicopter staying still in the air.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'cadf2f13-c0b9-46dd-b803-e4e11fdbe024'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- pleasantry
UPDATE public.words
SET translation = 'uprzejmość, grzecznościowa uwaga',
    definition = 'A polite, friendly remark made to be sociable, usually at the start of a conversation.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '1532ee60-8388-4882-8f22-82dad6ac96e5'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Business meetings and calls", "Meeting neighbours or acquaintances", "Describing small talk"]'::jsonb,
    synonyms = '["small talk", "polite remark", "courtesy"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Using the singular — it is almost always plural: *exchange pleasantries*.", "Thinking it means a pleasant event — it is only about polite words."]'::jsonb,
    example_sentences = '[{"context": "business", "sentence": "We *exchanged pleasantries* before the meeting started.", "translation": "Przed spotkaniem *wymieniliśmy uprzejmości*."}, {"context": "meeting", "sentence": "After a few *pleasantries*, they got down to business.", "translation": "Po kilku *grzecznościowych zdaniach* przeszli do konkretów."}, {"context": "negotiation", "sentence": "Let''s skip the *pleasantries* and talk about the contract.", "translation": "Darujmy sobie *uprzejmości* i porozmawiajmy o umowie."}]'::jsonb,
    collocations = '[{"phrase": "exchange pleasantries", "translation": "wymienić uprzejmości", "frequency": "VERY_COMMON"}, {"phrase": "skip the pleasantries", "translation": "pominąć uprzejmości", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈplezəntri", "syllables": "pleas-ant-ry", "stress": 1}'::jsonb,
    grammar = '{"pluralForm": "pleasantries"}'::jsonb,
    cultural_notes = 'Typical English small talk — weather, travel, weekend plans — is exactly what *pleasantries* are.',
    learning_tips = 'Learn the whole phrase *exchange pleasantries*.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '1532ee60-8388-4882-8f22-82dad6ac96e5'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- pick someone's brain
UPDATE public.words
SET translation = 'zasięgnąć czyjejś rady, wypytać kogoś',
    definition = 'To ask someone who knows a lot about a subject for advice or information.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'a9e851e6-c7b8-4d70-a285-5d8584735f28'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Asking an expert or colleague for advice", "Networking and informal meetings", "Starting a conversation at work"]'::jsonb,
    synonyms = '["ask for advice", "consult"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Taking it literally — it is a friendly request for advice, not something unpleasant.", "Forgetting the possessive — it is *pick your brain* / *pick her brain*, not *pick you*."]'::jsonb,
    example_sentences = '[{"context": "personal", "sentence": "Can I *pick your brain* about moving to Berlin?", "translation": "Mogę cię *podpytać* o przeprowadzkę do Berlina?"}, {"context": "networking", "sentence": "I''d love to *pick your brain* about marketing over coffee.", "translation": "Chętnie *zasięgnąłbym twojej rady* w sprawie marketingu przy kawie."}, {"context": "shopping", "sentence": "She *picked the engineer''s brain* before buying the car.", "translation": "Zanim kupiła auto, *wypytała* znajomego inżyniera."}]'::jsonb,
    collocations = '[{"phrase": "pick your brain about something", "translation": "podpytać cię o coś", "frequency": "VERY_COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = '{"irregularForms": {"pastTense": "picked", "presentParticiple": "picking"}}'::jsonb,
    cultural_notes = 'A casual, friendly phrase often used in emails and networking: *Could I pick your brain for 15 minutes?*',
    learning_tips = 'Imagine picking ideas out of someone''s head like fruit from a tree.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'a9e851e6-c7b8-4d70-a285-5d8584735f28'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- redundancy
UPDATE public.words
SET translation = 'zwolnienie z pracy, redundancja',
    definition = 'In British English, losing your job because your employer no longer needs your position; also the state of being unnecessary.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '1703194b-10c6-4b7e-a973-d9ec3b04b0ed'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["News about company layoffs", "Talking about your own job situation", "Technical talk about backup systems"]'::jsonb,
    synonyms = '["layoff", "dismissal", "duplication"]'::jsonb,
    antonyms = '["hiring", "recruitment"]'::jsonb,
    common_mistakes = '["Translating it as *nadmiar* in work contexts — *redundancy* there means *zwolnienie (z przyczyn ekonomicznych)*.", "Confusing *being made redundant* with being fired for bad performance — redundancy is about the job, not the person."]'::jsonb,
    example_sentences = '[{"context": "news", "sentence": "The factory announced fifty *redundancies*.", "translation": "Fabryka ogłosiła pięćdziesiąt *zwolnień*."}, {"context": "work", "sentence": "He was made *redundant* after twenty years.", "translation": "Po dwudziestu latach *stracił pracę w wyniku redukcji etatów*."}, {"context": "technology", "sentence": "The system has built-in *redundancy* in case a server fails.", "translation": "System ma wbudowaną *redundancję* na wypadek awarii serwera."}]'::jsonb,
    collocations = '[{"phrase": "redundancy pay", "translation": "odprawa", "frequency": "VERY_COMMON"}, {"phrase": "be made redundant", "translation": "zostać zwolnionym (redukcja etatów)", "frequency": "VERY_COMMON"}, {"phrase": "voluntary redundancy", "translation": "dobrowolne odejście", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "rɪˈdʌndənsi", "syllables": "re-dun-dan-cy", "stress": 2}'::jsonb,
    grammar = '{"pluralForm": "redundancies"}'::jsonb,
    cultural_notes = 'In the UK *redundancy* is the standard word for layoffs; Americans usually say *layoff*.',
    learning_tips = 'Remember the phrase *be made redundant* — it''s how British people say they lost their job.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '1703194b-10c6-4b7e-a973-d9ec3b04b0ed'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- upward trend
UPDATE public.words
SET definition = 'A steady increase in something over a period of time.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '427c18c0-c8c3-4245-ba82-a63eee6dd987'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Presenting sales or business results", "Talking about prices or statistics", "Discussing market data"]'::jsonb,
    synonyms = '["rise", "growth", "upturn"]'::jsonb,
    antonyms = '["downward trend", "decline"]'::jsonb,
    common_mistakes = '["Saying *an upward trend of sales* — the usual preposition is *in*: *an upward trend in sales*.", "Using *upwards trend* — the adjective form is *upward*."]'::jsonb,
    example_sentences = '[{"context": "business", "sentence": "Sales show a clear *upward trend* this quarter.", "translation": "Sprzedaż wykazuje w tym kwartale wyraźną *tendencję wzrostową*."}, {"context": "society", "sentence": "There''s an *upward trend* in remote work.", "translation": "Praca zdalna jest *w trendzie wzrostowym*."}, {"context": "economy", "sentence": "House prices continue their *upward trend*.", "translation": "Ceny mieszkań wciąż *rosną*."}]'::jsonb,
    collocations = '[{"phrase": "show an upward trend", "translation": "wykazywać tendencję wzrostową", "frequency": "VERY_COMMON"}, {"phrase": "steady upward trend", "translation": "stały wzrost", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = '{"pluralForm": "upward trends"}'::jsonb,
    cultural_notes = 'A typical phrase in reports, presentations and financial news.',
    learning_tips = 'Learn it in a pair: *upward trend* ↔ *downward trend*.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '427c18c0-c8c3-4245-ba82-a63eee6dd987'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- tipping point
UPDATE public.words
SET translation = 'punkt krytyczny, punkt zwrotny',
    definition = 'The moment when a series of small changes becomes big enough to cause a major change.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'fdd5aa1a-79d9-490a-8aab-c028fc98e350'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Discussing climate and social change", "Business and marketing trends", "Talking about personal decisions"]'::jsonb,
    synonyms = '["turning point", "critical point", "threshold"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Using it for any important moment — it means the point where change becomes unstoppable.", "Saying *tip point* — the form is *tipping point*."]'::jsonb,
    example_sentences = '[{"context": "environment", "sentence": "Scientists warn we are close to a climate *tipping point*.", "translation": "Naukowcy ostrzegają, że jesteśmy blisko klimatycznego *punktu krytycznego*."}, {"context": "politics", "sentence": "The scandal was the *tipping point* for his resignation.", "translation": "Skandal był *kroplą, która przelała czarę* i doprowadził do jego rezygnacji."}, {"context": "technology", "sentence": "Electric cars may soon reach a *tipping point*.", "translation": "Samochody elektryczne mogą wkrótce osiągnąć *punkt zwrotny*."}]'::jsonb,
    collocations = '[{"phrase": "reach a tipping point", "translation": "osiągnąć punkt krytyczny", "frequency": "VERY_COMMON"}, {"phrase": "climate tipping point", "translation": "klimatyczny punkt krytyczny", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈtɪpɪŋ pɔɪnt", "syllables": "tip-ping point", "stress": 1}'::jsonb,
    grammar = '{"pluralForm": "tipping points"}'::jsonb,
    cultural_notes = 'Popularised by Malcolm Gladwell''s book *The Tipping Point* (2000).',
    learning_tips = 'Picture a glass tipping over — a tiny push and suddenly it falls.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'fdd5aa1a-79d9-490a-8aab-c028fc98e350'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- to be on the up
UPDATE public.words
SET translation = 'mieć się coraz lepiej, iść w górę',
    definition = 'To be improving or becoming more successful.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '06f55bd8-3153-45ce-ae43-bbe05c59f3b3'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Talking about someone''s career", "Describing a recovering business or economy", "Sports commentary"]'::jsonb,
    synonyms = '["be improving", "be on the rise"]'::jsonb,
    antonyms = '["be in decline", "be on the way down"]'::jsonb,
    common_mistakes = '["Confusing it with American *on the up and up*, which means *honest, legal*.", "Using it without *be* — say *things are on the up*, not *things on the up*."]'::jsonb,
    example_sentences = '[{"context": "work", "sentence": "Her career is really *on the up*.", "translation": "Jej kariera naprawdę *idzie w górę*."}, {"context": "life", "sentence": "Things are *on the up* after a tough year.", "translation": "Po trudnym roku sprawy *mają się coraz lepiej*."}, {"context": "sport", "sentence": "The club has been *on the up* since the new coach arrived.", "translation": "Odkąd przyszedł nowy trener, klub *jest na fali wznoszącej*."}]'::jsonb,
    collocations = '[{"phrase": "things are on the up", "translation": "jest coraz lepiej", "frequency": "COMMON"}, {"phrase": "on the up and up (US)", "translation": "uczciwy, legalny", "frequency": "OCCASIONAL"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'Mostly British English and quite informal.',
    learning_tips = '*Up* = better: if something is *on the up*, it''s going upwards.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '06f55bd8-3153-45ce-ae43-bbe05c59f3b3'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- albeit
UPDATE public.words
SET translation = 'choć, aczkolwiek',
    definition = 'Although; used to add a fact that slightly limits what you have just said.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '978a5f4e-666f-4523-8fe4-423b95e884e9'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Formal writing and reports", "Academic essays", "Careful, balanced opinions"]'::jsonb,
    synonyms = '["although", "though", "even if"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Following it with a full clause (*albeit it was late*) — it is followed by a word or short phrase: *albeit late*, *albeit slowly*.", "Overusing it in casual speech — in conversation *though* sounds more natural."]'::jsonb,
    example_sentences = '[{"context": "conversation", "sentence": "He agreed, *albeit* reluctantly.", "translation": "Zgodził się, *choć* niechętnie."}, {"context": "report", "sentence": "The results were positive, *albeit* small.", "translation": "Wyniki były pozytywne, *aczkolwiek* niewielkie."}, {"context": "work", "sentence": "We finished the project, *albeit* two weeks late.", "translation": "Skończyliśmy projekt, *choć* z dwutygodniowym opóźnieniem."}]'::jsonb,
    collocations = '[{"phrase": "albeit slowly", "translation": "choć powoli", "frequency": "COMMON"}, {"phrase": "albeit briefly", "translation": "choć krótko", "frequency": "COMMON"}, {"phrase": "albeit reluctantly", "translation": "choć niechętnie", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ɔːlˈbiːɪt", "syllables": "al-be-it", "stress": 2}'::jsonb,
    grammar = NULL,
    cultural_notes = 'From Middle English *al be it* — "although it be". It sounds formal and is common in writing.',
    learning_tips = 'Use the pattern *X, albeit Y* where Y is just an adjective or adverb.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '978a5f4e-666f-4523-8fe4-423b95e884e9'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- cumbersome
UPDATE public.words
SET translation = 'nieporęczny, uciążliwy',
    definition = 'Large or heavy and difficult to carry; or slow and complicated to use.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '21106d94-d625-41ae-9e61-e3c27b42ee58'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Complaining about bureaucracy or procedures", "Describing heavy luggage or furniture", "Reviewing software or tools"]'::jsonb,
    synonyms = '["unwieldy", "bulky", "awkward"]'::jsonb,
    antonyms = '["handy", "convenient", "streamlined"]'::jsonb,
    common_mistakes = '["Translating it as *niewygodny* for things like chairs — *cumbersome* is about being hard to handle or use, not uncomfortable to sit on.", "Spelling it *cumbersom* or *cumbersume*."]'::jsonb,
    example_sentences = '[{"context": "bureaucracy", "sentence": "The application process is long and *cumbersome*.", "translation": "Proces składania wniosku jest długi i *uciążliwy*."}, {"context": "moving", "sentence": "We carried the *cumbersome* sofa up five floors.", "translation": "Wnieśliśmy tę *nieporęczną* kanapę na piąte piętro."}, {"context": "technology", "sentence": "The old software is *cumbersome* to use.", "translation": "Stary program jest *toporny* w obsłudze."}]'::jsonb,
    collocations = '[{"phrase": "cumbersome process", "translation": "uciążliwy proces", "frequency": "VERY_COMMON"}, {"phrase": "cumbersome procedure", "translation": "skomplikowana procedura", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈkʌmbərsəm", "syllables": "cum-ber-some", "stress": 1}'::jsonb,
    grammar = '{"comparativeForm": "more cumbersome", "superlativeForm": "most cumbersome"}'::jsonb,
    cultural_notes = 'Often used in workplace complaints about outdated systems or paperwork.',
    learning_tips = 'It *encumbers* you — slows you down and gets in the way.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '21106d94-d625-41ae-9e61-e3c27b42ee58'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- infringe
UPDATE public.words
SET translation = 'naruszać',
    definition = 'To break a law or rule, or to limit someone''s rights or freedom.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '98c2e981-3904-48dc-b892-978716d95a57'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Legal texts and contracts", "Copyright and intellectual property", "Debates about privacy and rights"]'::jsonb,
    synonyms = '["violate", "breach", "encroach on"]'::jsonb,
    antonyms = '["respect", "comply with", "obey"]'::jsonb,
    common_mistakes = '["Adding *on* with laws or copyright — *infringe a copyright*; use *infringe on/upon* with rights or freedom: *infringe on privacy*.", "Using it in everyday speech for small rules — *break a rule* is more natural."]'::jsonb,
    example_sentences = '[{"context": "legal", "sentence": "The app may *infringe* the company''s copyright.", "translation": "Aplikacja może *naruszać* prawa autorskie firmy."}, {"context": "rights", "sentence": "These cameras *infringe on* our privacy.", "translation": "Te kamery *naruszają* naszą prywatność."}, {"context": "business", "sentence": "They were accused of *infringing* the patent.", "translation": "Oskarżono ich o *naruszenie* patentu."}]'::jsonb,
    collocations = '[{"phrase": "infringe copyright", "translation": "naruszać prawa autorskie", "frequency": "VERY_COMMON"}, {"phrase": "infringe on someone''s rights", "translation": "naruszać czyjeś prawa", "frequency": "VERY_COMMON"}, {"phrase": "patent infringement", "translation": "naruszenie patentu", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ɪnˈfrɪndʒ", "syllables": "in-fringe", "stress": 2}'::jsonb,
    grammar = NULL,
    cultural_notes = 'Most common in legal and intellectual property contexts; the noun is *infringement*.',
    learning_tips = 'Learn the noun too: *copyright infringement* — you will see it on many websites.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '98c2e981-3904-48dc-b892-978716d95a57'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- permissible
UPDATE public.words
SET translation = 'dopuszczalny, dozwolony',
    definition = 'Allowed by rules or law.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'dbeed3d7-ba5c-4a77-9512-9972336ebd8a'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Official rules and regulations", "Workplace policies", "Legal and formal documents"]'::jsonb,
    synonyms = '["allowed", "permitted", "acceptable"]'::jsonb,
    antonyms = '["forbidden", "prohibited", "banned"]'::jsonb,
    common_mistakes = '["Using it in casual speech — *allowed* sounds much more natural in conversation.", "Spelling it *permissable* — the correct ending is *-ible*."]'::jsonb,
    example_sentences = '[{"context": "school", "sentence": "Is it *permissible* to bring food into the exam?", "translation": "Czy *wolno* wnosić jedzenie na egzamin?"}, {"context": "regulations", "sentence": "The *permissible* limit for this chemical is very low.", "translation": "*Dopuszczalny* limit tej substancji jest bardzo niski."}, {"context": "rules", "sentence": "Smoking is not *permissible* anywhere on the premises.", "translation": "Palenie nie jest *dozwolone* nigdzie na terenie obiektu."}]'::jsonb,
    collocations = '[{"phrase": "permissible limit", "translation": "dopuszczalny limit", "frequency": "VERY_COMMON"}, {"phrase": "legally permissible", "translation": "prawnie dopuszczalny", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "pərˈmɪsəbl", "syllables": "per-mis-si-ble", "stress": 2}'::jsonb,
    grammar = NULL,
    cultural_notes = 'Typical of official notices and regulations.',
    learning_tips = '*permit* → *permissible*: something you are permitted to do.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'dbeed3d7-ba5c-4a77-9512-9972336ebd8a'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- admissible
UPDATE public.words
SET translation = 'dopuszczalny',
    definition = 'Accepted as valid, especially about evidence that a court allows to be considered.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '1b7f72a2-ee9a-4258-9681-05ef20728de6'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Courtrooms and legal dramas", "Legal articles and news", "Formal decisions about what is accepted"]'::jsonb,
    synonyms = '["acceptable", "allowable", "valid"]'::jsonb,
    antonyms = '["inadmissible", "unacceptable"]'::jsonb,
    common_mistakes = '["Confusing it with *permissible* — *admissible* is mainly about evidence or arguments being accepted, not general permission.", "Spelling it *admissable* — the correct ending is *-ible*."]'::jsonb,
    example_sentences = '[{"context": "legal", "sentence": "The recording is not *admissible* in court.", "translation": "To nagranie nie jest *dopuszczalne* jako dowód w sądzie."}, {"context": "courtroom", "sentence": "The judge ruled the evidence *admissible*.", "translation": "Sędzia uznał dowód za *dopuszczalny*."}, {"context": "formal", "sentence": "Only *admissible* claims will be reviewed.", "translation": "Rozpatrywane będą tylko *dopuszczalne* roszczenia."}]'::jsonb,
    collocations = '[{"phrase": "admissible evidence", "translation": "dopuszczalny dowód", "frequency": "VERY_COMMON"}, {"phrase": "admissible in court", "translation": "dopuszczalny w sądzie", "frequency": "VERY_COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ədˈmɪsəbl", "syllables": "ad-mis-si-ble", "stress": 2}'::jsonb,
    grammar = NULL,
    cultural_notes = 'Very frequent in courtroom dramas; its opposite *inadmissible* is just as common.',
    learning_tips = '*admit* → *admissible*: the court can admit it.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '1b7f72a2-ee9a-4258-9681-05ef20728de6'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- spur
UPDATE public.words
SET translation = 'bodziec, impuls',
    definition = 'Something that encourages a person or activity to develop or happen faster.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'bd980035-cab9-4b51-9aa0-2c429ec3ca3c'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Talking about motivation", "Economic news about growth", "Spontaneous decisions (spur of the moment)"]'::jsonb,
    synonyms = '["incentive", "stimulus", "motivation"]'::jsonb,
    antonyms = '["deterrent", "obstacle"]'::jsonb,
    common_mistakes = '["Saying *in the spur of the moment* — the phrase is *on the spur of the moment*.", "Forgetting that *spur* is also a verb: *The prize spurred her on*."]'::jsonb,
    example_sentences = '[{"context": "motivation", "sentence": "The award was a *spur* to work even harder.", "translation": "Nagroda była *bodźcem* do jeszcze cięższej pracy."}, {"context": "travel", "sentence": "We booked the trip on the *spur* of the moment.", "translation": "Zarezerwowaliśmy wycieczkę *pod wpływem chwili*."}, {"context": "economy", "sentence": "Lower taxes could be a *spur* to growth.", "translation": "Niższe podatki mogą być *impulsem* do wzrostu."}]'::jsonb,
    collocations = '[{"phrase": "on the spur of the moment", "translation": "pod wpływem chwili", "frequency": "VERY_COMMON"}, {"phrase": "a spur to growth", "translation": "impuls do wzrostu", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "spɜːr", "syllables": "spur", "stress": 1}'::jsonb,
    grammar = '{"pluralForm": "spurs"}'::jsonb,
    cultural_notes = 'Literally, a *spur* is a small metal point on a rider''s boot used to make a horse go faster.',
    learning_tips = 'Learn the very common *on the spur of the moment* — a spontaneous decision.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'bd980035-cab9-4b51-9aa0-2c429ec3ca3c'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- brick-and-mortar
UPDATE public.words
SET type = 'ADJECTIVE',
    translation = 'stacjonarny',
    definition = 'Describes a business that has a physical shop or office, rather than operating only online.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '69b5ddeb-e688-476e-9e90-e0b223be0c52'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Comparing online and physical shops", "Retail and e-commerce news", "Talking about where you prefer to shop"]'::jsonb,
    synonyms = '["physical", "high-street", "offline"]'::jsonb,
    antonyms = '["online", "e-commerce"]'::jsonb,
    common_mistakes = '["Using it as a noun (*a brick-and-mortar*) — it describes a noun: *a brick-and-mortar store*.", "Using it for buildings in general — it is about businesses, in contrast to online ones."]'::jsonb,
    example_sentences = '[{"context": "business", "sentence": "They opened their first *brick-and-mortar* store after years online.", "translation": "Po latach działania w sieci otworzyli pierwszy sklep *stacjonarny*."}, {"context": "news", "sentence": "*Brick-and-mortar* retail is struggling.", "translation": "Handel *stacjonarny* przeżywa trudności."}, {"context": "personal", "sentence": "I prefer *brick-and-mortar* bookshops to ordering online.", "translation": "Wolę księgarnie *stacjonarne* od zamawiania przez internet."}]'::jsonb,
    collocations = '[{"phrase": "brick-and-mortar store", "translation": "sklep stacjonarny", "frequency": "VERY_COMMON"}, {"phrase": "brick-and-mortar retail", "translation": "handel stacjonarny", "frequency": "COMMON"}]'::jsonb,
    pronunciation = NULL,
    grammar = NULL,
    cultural_notes = 'The phrase became popular with the rise of e-commerce, to contrast physical shops with online ones.',
    learning_tips = 'Bricks and mortar are what real buildings are made of — the opposite of a website.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '69b5ddeb-e688-476e-9e90-e0b223be0c52'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- certainty
UPDATE public.words
SET definition = 'The feeling of being completely sure about something; also a fact that is definitely true.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'c2c51bc2-d3a1-4bfb-83f3-488dd85159f8'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Expressing how sure you are", "Discussing predictions and decisions", "Formal or analytical writing"]'::jsonb,
    synonyms = '["confidence", "assurance", "sureness"]'::jsonb,
    antonyms = '["doubt", "uncertainty"]'::jsonb,
    common_mistakes = '["Saying *with a certainty* — the phrase is *with certainty* (no article).", "Confusing *certainty* with *certificate* — they share a root but mean different things."]'::jsonb,
    example_sentences = '[{"context": "conversation", "sentence": "I can''t say with *certainty* when he''ll be back.", "translation": "Nie mogę powiedzieć *na pewno*, kiedy wróci."}, {"context": "economy", "sentence": "There''s no *certainty* that prices will fall.", "translation": "Nie ma *pewności*, że ceny spadną."}, {"context": "sport", "sentence": "Victory seemed a *certainty* until the last minute.", "translation": "Zwycięstwo wydawało się *pewne* aż do ostatniej minuty."}]'::jsonb,
    collocations = '[{"phrase": "with certainty", "translation": "z pewnością, na pewno", "frequency": "VERY_COMMON"}, {"phrase": "absolute certainty", "translation": "absolutna pewność", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈsɜːrtnti", "syllables": "cer-tain-ty", "stress": 1}'::jsonb,
    grammar = '{"pluralForm": "certainties"}'::jsonb,
    cultural_notes = 'Famous saying: *Nothing is certain except death and taxes* (Benjamin Franklin).',
    learning_tips = '*certain* + *-ty* → the noun, just like *safe → safety*.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'c2c51bc2-d3a1-4bfb-83f3-488dd85159f8'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- palpable
UPDATE public.words
SET translation = 'wyczuwalny, namacalny',
    definition = 'So strong or obvious that it can almost be felt.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'fe21fe74-b689-4dfb-a2be-1e4d0f85c828'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Describing tension or excitement in a room", "Writing about emotions and atmosphere", "News and sports commentary"]'::jsonb,
    synonyms = '["tangible", "noticeable", "obvious"]'::jsonb,
    antonyms = '["imperceptible", "subtle", "intangible"]'::jsonb,
    common_mistakes = '["Using it for physical objects — it is mostly used for feelings and atmosphere: *palpable tension*.", "Translating it as *namacalny* for emotions — *wyczuwalny* sounds more natural there."]'::jsonb,
    example_sentences = '[{"context": "atmosphere", "sentence": "The tension in the room was *palpable*.", "translation": "Napięcie w pokoju było *wyczuwalne*."}, {"context": "event", "sentence": "There was *palpable* excitement before the concert.", "translation": "Przed koncertem panowało *wyczuwalne* podekscytowanie."}, {"context": "emotions", "sentence": "Her relief was *palpable* when she heard the news.", "translation": "Jej ulga była *wyraźnie widoczna*, gdy usłyszała wiadomość."}]'::jsonb,
    collocations = '[{"phrase": "palpable tension", "translation": "wyczuwalne napięcie", "frequency": "VERY_COMMON"}, {"phrase": "palpable excitement", "translation": "wyczuwalne podekscytowanie", "frequency": "COMMON"}, {"phrase": "palpable sense of relief", "translation": "wyraźna ulga", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈpælpəbl", "syllables": "pal-pa-ble", "stress": 1}'::jsonb,
    grammar = NULL,
    cultural_notes = 'From Latin *palpare* — "to touch". Writers use it for very strong atmosphere.',
    learning_tips = 'Collocation to memorise: *the tension was palpable*.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'fe21fe74-b689-4dfb-a2be-1e4d0f85c828'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- lull
UPDATE public.words
SET translation = 'ukołysać, uśpić',
    definition = 'To make someone feel calm or sleepy, or to make them feel safe when they shouldn''t.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '34ad29df-1ab0-4450-9852-2239aeea4e9c'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Talking about babies and sleep", "Describing calming sounds or music", "Warning about a false sense of security"]'::jsonb,
    synonyms = '["soothe", "calm", "rock to sleep"]'::jsonb,
    antonyms = '["wake", "alarm", "agitate"]'::jsonb,
    common_mistakes = '["Forgetting the pattern *lull someone to sleep* or *lull someone into a false sense of security*.", "Confusing the verb with the noun *a lull* (a quiet pause) — both exist."]'::jsonb,
    example_sentences = '[{"context": "everyday", "sentence": "The sound of the rain *lulled* me to sleep.", "translation": "Szum deszczu *ukołysał* mnie do snu."}, {"context": "family", "sentence": "She sang softly to *lull* the baby to sleep.", "translation": "Śpiewała cicho, żeby *ukołysać* dziecko do snu."}, {"context": "games", "sentence": "Don''t let the easy first level *lull* you into a false sense of security.", "translation": "Nie daj się *uśpić* łatwemu pierwszemu poziomowi."}]'::jsonb,
    collocations = '[{"phrase": "lull someone to sleep", "translation": "ukołysać kogoś do snu", "frequency": "VERY_COMMON"}, {"phrase": "lull into a false sense of security", "translation": "uśpić czyjąś czujność", "frequency": "COMMON"}, {"phrase": "a lull in the conversation", "translation": "chwila ciszy w rozmowie", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "lʌl", "syllables": "lull", "stress": 1}'::jsonb,
    grammar = NULL,
    cultural_notes = 'The noun appears in *the lull before the storm* — the calm before trouble begins.',
    learning_tips = '*Lullaby* (kołysanka) comes from the same word.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '34ad29df-1ab0-4450-9852-2239aeea4e9c'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- ruminate
UPDATE public.words
SET translation = 'rozmyślać, roztrząsać',
    definition = 'To think about something deeply and for a long time, often about worries or the past.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'f6151878-c7b8-4141-8885-cb12552dbfd7'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Talking about stress, worry or overthinking", "Psychology and wellbeing", "Describing careful decision-making"]'::jsonb,
    synonyms = '["ponder", "brood", "dwell on"]'::jsonb,
    antonyms = '["ignore", "let go"]'::jsonb,
    common_mistakes = '["Forgetting the preposition — *ruminate on/about/over something*.", "Using it casually — it sounds formal; in conversation *overthink* or *dwell on* is more common."]'::jsonb,
    example_sentences = '[{"context": "wellbeing", "sentence": "I tend to *ruminate* on my mistakes at night.", "translation": "W nocy często *roztrząsam* swoje błędy."}, {"context": "decisions", "sentence": "He *ruminated* over the offer for weeks before deciding.", "translation": "Przez tygodnie *rozmyślał* nad ofertą, zanim się zdecydował."}, {"context": "advice", "sentence": "Try not to *ruminate* — talk to someone instead.", "translation": "Staraj się nie *rozpamiętywać* — lepiej z kimś porozmawiaj."}]'::jsonb,
    collocations = '[{"phrase": "ruminate on", "translation": "rozmyślać nad", "frequency": "COMMON"}, {"phrase": "ruminate about the past", "translation": "rozpamiętywać przeszłość", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈruːmɪneɪt", "syllables": "ru-mi-nate", "stress": 1}'::jsonb,
    grammar = NULL,
    cultural_notes = 'Psychologists use *rumination* for repetitive negative thinking linked to anxiety.',
    learning_tips = 'Cows *ruminate* (chew the same food again and again) — people ruminate on the same thoughts.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = 'f6151878-c7b8-4141-8885-cb12552dbfd7'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- chamomile
UPDATE public.words
SET extra_mark = NULL,
    updated_at = CURRENT_TIMESTAMP
WHERE id = '3c1038c6-e343-4bbb-8e92-554e5fc02707'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Ordering tea in a café", "Talking about sleep and relaxation", "Gardening and herbs"]'::jsonb,
    synonyms = '["camomile"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Pronouncing the *ch* as in *chair* — it is pronounced like *k*: KAM-uh-mile.", "Saying *chamomile tea* is only for illness — in English-speaking countries it is a common everyday evening drink."]'::jsonb,
    example_sentences = '[{"context": "everyday", "sentence": "I drink *chamomile* tea before bed.", "translation": "Przed snem piję herbatę z *rumianku*."}, {"context": "café", "sentence": "Could I have a *chamomile*, please?", "translation": "Poproszę *rumianek*."}, {"context": "gardening", "sentence": "She grows *chamomile* and mint in her garden.", "translation": "Uprawia w ogrodzie *rumianek* i miętę."}]'::jsonb,
    collocations = '[{"phrase": "chamomile tea", "translation": "herbata rumiankowa", "frequency": "VERY_COMMON"}, {"phrase": "a cup of chamomile", "translation": "filiżanka rumianku", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ˈkæməmaɪl", "syllables": "cham-o-mile", "stress": 1}'::jsonb,
    grammar = '{"pluralForm": "chamomiles"}'::jsonb,
    cultural_notes = 'A very popular herbal tea in the UK and US, usually drunk in the evening to relax.',
    learning_tips = 'British spelling *camomile* shows the real pronunciation — it starts with a *k* sound.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '3c1038c6-e343-4bbb-8e92-554e5fc02707'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- vocational school
UPDATE public.words
SET definition = 'A school that teaches practical skills for a particular job, such as electrician or chef.',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '87ed9d59-5da6-4b76-a0e8-21c314589ea3'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

UPDATE public.word_details
SET use_cases = '["Talking about education and career paths", "Job interviews and CVs", "Discussing the education system"]'::jsonb,
    synonyms = '["trade school", "technical college"]'::jsonb,
    antonyms = '["university", "academic school"]'::jsonb,
    common_mistakes = '["Using *professional school* — in English that often means law or medical school; for practical trades say *vocational school* or *trade school*.", "Spelling it *vocal school* — *vocal* is about the voice."]'::jsonb,
    example_sentences = '[{"context": "career", "sentence": "He went to *vocational school* to become an electrician.", "translation": "Poszedł do *szkoły zawodowej*, żeby zostać elektrykiem."}, {"context": "education", "sentence": "*Vocational schools* offer a lot of hands-on training.", "translation": "*Szkoły zawodowe* oferują dużo zajęć praktycznych."}, {"context": "personal story", "sentence": "After *vocational school*, she opened her own bakery.", "translation": "Po *szkole zawodowej* otworzyła własną piekarnię."}]'::jsonb,
    collocations = '[{"phrase": "attend vocational school", "translation": "chodzić do szkoły zawodowej", "frequency": "COMMON"}, {"phrase": "vocational training", "translation": "kształcenie zawodowe", "frequency": "VERY_COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "vəʊˈkeɪʃənl skuːl", "syllables": "vo-ca-tion-al school", "stress": 2}'::jsonb,
    grammar = '{"pluralForm": "vocational schools"}'::jsonb,
    cultural_notes = 'In the US *trade school* is a common informal name; in the UK people often say *college* or *technical college*.',
    learning_tips = '*Vocation* means a job you are trained for — *vocational* = related to a job.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '87ed9d59-5da6-4b76-a0e8-21c314589ea3'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';

-- shed
UPDATE public.word_details
SET use_cases = '["Talking about your garden or backyard", "Describing where tools or bikes are kept", "DIY and home projects"]'::jsonb,
    synonyms = '["hut", "shack", "outbuilding"]'::jsonb,
    antonyms = '[]'::jsonb,
    common_mistakes = '["Confusing the noun *a shed* (szopa) with the verb *to shed* (zrzucać, np. skórę) — they look the same but mean different things.", "Using *shed* for a large building — a *shed* is small and simple; a big farm building is a *barn*."]'::jsonb,
    example_sentences = '[{"context": "home", "sentence": "The tools are kept in the *shed*.", "translation": "Narzędzia trzymamy w *szopie*."}, {"context": "DIY", "sentence": "We built a small garden *shed* last summer.", "translation": "Zeszłego lata postawiliśmy małą *szopę* w ogrodzie."}, {"context": "everyday", "sentence": "Can you put your bike in the *shed*?", "translation": "Możesz wstawić rower do *szopy*?"}]'::jsonb,
    collocations = '[{"phrase": "garden shed", "translation": "szopa ogrodowa", "frequency": "VERY_COMMON"}, {"phrase": "tool shed", "translation": "szopa na narzędzia", "frequency": "COMMON"}, {"phrase": "bike shed", "translation": "wiata na rowery", "frequency": "COMMON"}]'::jsonb,
    pronunciation = '{"ipa": "ʃed", "syllables": "shed", "stress": 1}'::jsonb,
    grammar = '{"pluralForm": "sheds"}'::jsonb,
    cultural_notes = 'In the UK the garden *shed* is a cultural symbol — a quiet place where people do hobbies or DIY.',
    learning_tips = 'Be ready for the verb too: *shed light on* (rzucić światło na) and *shed tears* (ronić łzy) use the same spelling.',
    updated_at = CURRENT_TIMESTAMP
WHERE word_id = '0a2fb6cb-eba1-404e-8888-f810be0f2f2d'
  AND user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4';


-------------
-- V17 captured words: move inline "Example: ..." out of definitions, drop wrong register marks
-------------
UPDATE public.words
SET definition = regexp_replace(definition, '\s*Example:.*$', ''),
    updated_at = CURRENT_TIMESTAMP
WHERE user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4'
  AND definition LIKE '%Example:%';

UPDATE public.words
SET extra_mark = NULL,
    updated_at = CURRENT_TIMESTAMP
WHERE user_id = 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4'
  AND source_word IN ('ubiquitous', 'procrastinate', 'verbose', 'melancholy');
