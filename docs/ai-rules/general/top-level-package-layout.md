# Top-level package layout

All production code lives under `src/main/kotlin/com/ord/`. The only file directly in that root package is `Application.kt`; everything else belongs to one of five top-level packages: `config/`, `core/`, `features/`, `exceptions/`, `shared/`. Do not invent new top-level packages.

## Good

```text
src/main/kotlin/com/ord/
├── Application.kt        # @SpringBootApplication entry point + main()
├── config/              # Spring config + @ConfigurationProperties
├── core/                # foundational domain concerns (auth, user, word, ...)
├── features/            # self-contained feature modules
├── exceptions/          # custom exceptions + handlers
└── shared/              # generic, domain-agnostic infrastructure
```

## Bad

```text
src/main/kotlin/com/ord/
├── Application.kt
├── controllers/         # no global "controllers" package — controllers live inside their feature/core module
├── utils/               # belongs under shared/utils
├── WordEntity.kt        # domain classes must live in core/ or features/, not the root package
└── services/            # no global "services" package
```
