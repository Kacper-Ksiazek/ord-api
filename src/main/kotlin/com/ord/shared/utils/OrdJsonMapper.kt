package com.ord.shared.utils

import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinFeature
import tools.jackson.module.kotlin.kotlinModule

object OrdJsonMapper {
    fun configureKotlinModule(builder: JsonMapper.Builder): JsonMapper.Builder =
        builder.addModule(
            kotlinModule {
                configure(KotlinFeature.NullIsSameAsDefault, true)
                disable(KotlinFeature.StrictNullChecks)
                disable(KotlinFeature.NewStrictNullChecks)
            }
        )

    val instance: JsonMapper = configureKotlinModule(JsonMapper.builder()).build()
}
