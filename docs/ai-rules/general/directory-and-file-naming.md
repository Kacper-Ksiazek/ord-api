# Directory and file naming

Directory (package) names are lower `snake_case` — multi-word segments use underscores (`bank_group`, `gpt_tokens_usage`, `user_activity_log`, `word_details`, `ai_message_tips`). Kotlin file names are `PascalCase` and match the single top-level type they declare (`WordEntity.kt`, `ConversationCRUDFacade.kt`). Never use camelCase or kebab-case for directories, and never use snake_case for `.kt` file names.

## Good

```text
features/user_activity_log/                 # snake_case package
└── models/
    └── activity_log/
        ├── UserActivityLogEntity.kt        # PascalCase file = type name
        ├── UserActivityLogDTO.kt
        └── enums/
            └── UserActivityType.kt
```

## Bad

```text
features/userActivityLog/                    # camelCase directory
└── models/
    └── activity-log/                        # kebab-case directory
        ├── user_activity_log_entity.kt      # snake_case file name
        └── UserActivityType_Enum.kt         # name doesn't match the declared type
```
