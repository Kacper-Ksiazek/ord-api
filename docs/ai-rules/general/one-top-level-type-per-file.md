# One top-level type per file, package mirrors directory

Each `.kt` file declares exactly one top-level type (class, interface, enum, or sealed interface) whose name equals the file name. The `package` declaration must mirror the file's directory path exactly under `com.ord`. Free-standing top-level functions are acceptable only as small helpers/extensions in a clearly named file (e.g. `ListExtensions.kt`).

## Good

```kotlin
// File: src/main/kotlin/com/ord/core/word/models/word/enums/WordType.kt
package com.ord.core.word.models.word.enums

@Schema(description = "Type of word or expression")
@ExportToOpenAPI
enum class WordType {
    NOUN,
    VERB,
    ADJECTIVE,
    ADVERB,
    IDIOM,
    PHRASE,
}
```

## Bad

```kotlin
// File: src/main/kotlin/com/ord/core/word/models/word/enums/WordType.kt
package com.ord.core.word.models // WRONG: package does not match the directory

enum class WordType { NOUN, VERB }

class WordExtraMark   // WRONG: second top-level type in the file, and file name != type name
```
