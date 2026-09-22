package com.ord.core.auth.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.ord.shared.annotations.ExportToOpenAPI
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "UI locale for user-facing content (matches ord-frontend Paraglide: en, pl, de)")
@ExportToOpenAPI
enum class UiLocale(val languageTag: String) {
    EN("en"),
    PL("pl"),
    DE("de");

    @JsonValue
    fun toJson(): String = languageTag

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromJson(value: String?): UiLocale? {
            if (value == null) {
                return null
            }
            return entries.find { it.languageTag == value.lowercase() }
                ?: throw IllegalArgumentException("Unsupported locale: $value")
        }

        fun resolve(value: UiLocale?): UiLocale = value ?: EN
    }
}
