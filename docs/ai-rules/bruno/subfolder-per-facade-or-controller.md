# Subdivide domain folders by facade or controller

Within a domain, create subfolders that mirror how the Kotlin API layer splits responsibility — either a **separate controller** (e.g. `WordCRUDController` vs `WordAIController`) or a **distinct facade** injected into one controller (e.g. `ConversationAuxiliaryFacade`, `ConversationCRUDFacade`). Subfolder names are kebab-case, stripped of the `Facade` suffix.

## Good

```
conversations/
  auxiliary/     # ConversationAuxiliaryFacade
  crud/          # ConversationCRUDFacade
  activity/      # ConversationActivityFacade
  ongoing/       # OngoingConversationController → OngoingConversationFacade
```

```
words/
  crud/          # WordCRUDController
  details/       # WordDetailsController
  ai/            # WordAIController
```

## Bad

```
conversations/
  suggest-topics-sse.bru      # flat — hides facade/controller structure
  create.bru
  ongoing-ai-initialize.bru
```
