# Import ordering

Imports are a single block sorted alphabetically by fully-qualified name (IntelliJ default), with no blank lines separating groups and no manual grouping. In practice `com.ord.*` comes first, then third-party packages (`io.*`, `jakarta.*`, `org.springframework.*`, `reactor.*`), and `java.*` / `javax.*` last. Prefer explicit single-type imports; the only wildcard import used in this codebase is `java.util.*`.

## Good

```kotlin
package com.ord.core.word.models.word

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordType
import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*
```

## Bad

```kotlin
package com.ord.core.word.models.word

import java.time.Instant                       // java.* must come last, not first
import org.springframework.data.annotation.Id

import com.ord.shared.models.IdentifiableUserResource  // blank-line grouping is not used
import com.ord.core.word.models.word.enums.*    // avoid wildcard imports for project packages
```
