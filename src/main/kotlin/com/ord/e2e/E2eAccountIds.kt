package com.ord.e2e

import java.util.UUID

object E2eAccountIds {
    fun userId(email: String): UUID =
        UUID.nameUUIDFromBytes("ord-e2e:$email".toByteArray(Charsets.UTF_8))

    fun proficiencyId(email: String): UUID =
        UUID.nameUUIDFromBytes("ord-e2e-proficiency:$email".toByteArray(Charsets.UTF_8))
}
