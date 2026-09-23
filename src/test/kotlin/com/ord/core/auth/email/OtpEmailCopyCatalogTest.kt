package com.ord.core.auth.email

import com.ord.core.auth.model.enums.UiLocale
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("OtpEmailCopyCatalog")
class OtpEmailCopyCatalogTest {
    @Test
    fun `should return Polish copy for PL locale`() {
        val copy = OtpEmailCopyCatalog.forLocale(UiLocale.PL)

        copy.htmlLang shouldBe "pl"
        copy.heading shouldBe "Wprowadź swój kod"
        copy.buttonLabel shouldBe "Zaloguj się kodem"
    }

    @Test
    fun `should return German copy for DE locale`() {
        val copy = OtpEmailCopyCatalog.forLocale(UiLocale.DE)

        copy.htmlLang shouldBe "de"
        copy.heading shouldBe "Geben Sie Ihren Code ein"
    }
}
