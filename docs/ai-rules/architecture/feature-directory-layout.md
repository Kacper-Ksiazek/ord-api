# Lay out a new feature as a complete vertical slice

A new feature lives under `features/<feature>/` and is split into `api/` (controller + facades + requests), `models/<entity>/` (entity, DTO, mapper, enums), `repositories/` (+ `impl/`), and `services/` (+ `impl/`). Keep every layer for the feature inside this directory rather than scattering classes into shared/global packages, so the slice stays self-contained.

## Good

```text
features/conversation/
├── api/
│   ├── ConversationController.kt
│   ├── facades/
│   │   ├── ConversationCRUDFacade.kt
│   │   └── impl/ConversationCRUDFacadeImpl.kt
│   └── requests/
│       └── CreateConversationRequest.kt
├── models/
│   └── conversation/
│       ├── ConversationEntity.kt
│       ├── ConversationDTO.kt
│       └── ConversationMapper.kt
├── repositories/
│   ├── ConversationRepository.kt
│   └── impl/ConversationRepositoryCustomMethodsImpl.kt
└── services/
    ├── ConversationService.kt
    └── impl/ConversationServiceImpl.kt
```

## Bad

```text
# Layers spread across global packages instead of one feature slice
controllers/ConversationController.kt
dtos/ConversationDTO.kt
services/ConversationServiceImpl.kt
repositories/ConversationRepository.kt
```
