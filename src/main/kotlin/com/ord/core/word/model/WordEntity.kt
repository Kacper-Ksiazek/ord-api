package com.ord.core.word.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.core.word.model.json.ExampleSentence
import com.ord.features.bank.model.BankEntity
import com.ord.features.bank_group.model.BankGroupEntity
import com.ord.shared.models.IdentifiableUserResource
import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("words")
data class WordEntity(
    @Id
    override val id: UUID? = null,

    var origin: String,
    var translation: String,
    var definition: String,

    var isCompleted: Boolean = false,
    var isBookmarked: Boolean = false,

    var points: Int = 0,

    var type: WordType,
    var extraMark: WordExtraMark? = null,
    var translatedTo: LanguageName,
    var translatedFrom: LanguageName,

    var useCases: Json = Json.of("[]"),
    var exampleSentences: Json = Json.of("[]"),

    var bankId: UUID? = null,
    var bankGroupId: UUID? = null,
    override var userId: UUID,

    var completedAt: Instant? = null,
    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
) : IdentifiableUserResource