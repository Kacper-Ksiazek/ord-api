# Declare an interface, place the implementation in an `impl/` subpackage

Facades, services, and repository custom-method contracts are always defined as an interface in the layer package, with the concrete class in a sibling `impl/` subpackage. The implementation class name is the interface name suffixed with `Impl`. Inject and reference the interface type everywhere; only the Spring-annotated impl class lives in `impl/`.

## Good

```text
api/facades/
├── ConversationCRUDFacade.kt          # interface
└── impl/
    └── ConversationCRUDFacadeImpl.kt  # @Service ... : ConversationCRUDFacade

services/
├── ConversationService.kt             # interface
└── impl/
    └── ConversationServiceImpl.kt     # @Service ... : ConversationService
```

```kotlin
// package com.ord.features.conversation.services.impl
@Service
class ConversationServiceImpl(
    private val conversationRepository: ConversationRepository
) : ConversationService { ... }
```

## Bad

```kotlin
// package com.ord.features.conversation.services
// No interface, concrete class injected directly and living outside impl/
@Service
class ConversationService(
    private val conversationRepository: ConversationRepository
) { ... }
```
