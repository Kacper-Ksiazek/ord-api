package com.ord.shared.utils

import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinFeature
import tools.jackson.module.kotlin.kotlinModule

object OrdJsonMapper {
    /**
     * Relaxed Kotlin null handling for fixtures, AI JSON, stubs, and other non-HTTP mappers.
     */
    fun configureRelaxedKotlinModule(builder: JsonMapper.Builder): JsonMapper.Builder =
        builder.addModule(
            kotlinModule {
                configure(KotlinFeature.NullIsSameAsDefault, true)
                disable(KotlinFeature.StrictNullChecks)
                disable(KotlinFeature.NewStrictNullChecks)
            }
        )

    /**
     * HTTP codec profile: allow nullable collection elements (e.g. crossword board cells)
     * without treating missing request fields as Kotlin defaults.
     */
    fun configureHttpKotlinModule(builder: JsonMapper.Builder): JsonMapper.Builder =
        builder.addModule(
            kotlinModule {
                disable(KotlinFeature.StrictNullChecks)
                disable(KotlinFeature.NewStrictNullChecks)
            }
        )

    val instance: JsonMapper = configureRelaxedKotlinModule(JsonMapper.builder()).build()
}
