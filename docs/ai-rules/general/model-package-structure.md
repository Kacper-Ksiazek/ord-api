# Model package structure

Within a module, domain types live under `models/<entity_snake_case>/`, one directory per entity. That directory holds the `<Entity>Entity.kt`, `<Entity>DTO.kt`, and `<Entity>Mapper.kt` together, with `enums/` for enum types and `jsonb/` for value classes persisted as JSONB columns. Do not scatter an entity's enums or JSONB value types across unrelated packages.

## Good

```text
core/word/models/word_details/
├── WordDetailsEntity.kt
├── WordDetailsDTO.kt
├── WordDetailsCompactDTO.kt
├── WordDetailsMapper.kt
├── enums/
│   ├── WordGender.kt
│   └── WordCollocationFrequency.kt
└── jsonb/
    ├── ExampleSentence.kt
    ├── WordCollocation.kt
    └── WordConjugation.kt
```

## Bad

```text
core/word/
├── WordDetailsEntity.kt          # entity not under models/<entity>/
├── dtos/WordDetailsDTO.kt        # DTO split into a sibling "dtos" package
├── enums/WordGender.kt           # enum pulled up to a module-wide enums package
└── models/word_details/
    └── WordCollocation.kt        # JSONB value class missing its jsonb/ subpackage
```
