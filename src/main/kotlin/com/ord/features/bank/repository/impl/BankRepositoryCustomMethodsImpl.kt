package com.ord.features.bank.repository.impl

import com.ord.features.bank.api.responses.BankListItem
import com.ord.features.bank.repository.BankRepositoryCustomMethods
import com.ord.features.bank_group.dto.BankGroupCompact
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.util.UUID

@Repository
class BankRepositoryCustomMethodsImpl(
    private val template: R2dbcEntityTemplate,
) : BankRepositoryCustomMethods {
    override fun findAllListItemsByUserId(userId: UUID): Flux<BankListItem> {
        val query = """
            SELECT
                b.id AS id,
                b.name AS name,
                bg.name AS bank_group_name,
                bg.color AS bank_group_color
            FROM banks b
            LEFT JOIN bank_groups bg ON b.group_id = bg.id AND bg.user_id = b.user_id
            WHERE b.user_id = :userId
            ORDER BY b.name ASC
        """

        return template.databaseClient
            .sql(query)
            .bind("userId", userId)
            .map { row, _ ->
                val hasBankGroup = row.get("bank_group_name", String::class.java) != null

                BankListItem(
                    id = row.get("id", UUID::class.java)!!,
                    name = row.get("name", String::class.java)!!,
                    bankGroup = if (hasBankGroup) {
                        BankGroupCompact(
                            name = row.get("bank_group_name", String::class.java)!!,
                            color = row.get("bank_group_color", String::class.java)!!,
                        )
                    } else {
                        null
                    },
                )
            }
            .all()
    }
}
