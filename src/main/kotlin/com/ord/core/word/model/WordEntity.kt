package com.ord.core.word.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.core.word.model.json.ExampleSentence
import com.ord.features.bank.model.BankEntity
import com.ord.features.bank_group.model.BankGroupEntity
import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("words")
data class WordEntity(
    @Id
    override val id: UUID = UUID.randomUUID(),

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

    var useCases: Set<String> = emptySet(),
    var exampleSentences: Set<ExampleSentence> = emptySet(),

    var bankId: UUID? = null,
    var bankGroupId: UUID? = null,
    override var userId: UUID,

    var completedAt: Instant? = null,
    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),

    @Transient override var user: UserEntity,
    @Transient var bank: BankEntity? = null,
    @Transient var bankGroup: BankGroupEntity? = null,
) : IdentifiableUserResource