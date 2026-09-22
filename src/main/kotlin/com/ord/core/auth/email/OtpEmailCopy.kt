package com.ord.core.auth.email

import com.ord.core.auth.model.enums.UiLocale

data class OtpEmailCopy(
    val htmlLang: String,
    val subject: String,
    val preheader: String,
    val heading: String,
    val intro: String,
    val buttonLabel: String,
    val expiryNote: String,
    val ignoreNote: String,
    val footerTagline: String,
)

object OtpEmailCopyCatalog {
    fun forLocale(locale: UiLocale): OtpEmailCopy =
        when (locale) {
            UiLocale.EN -> OtpEmailCopy(
                htmlLang = "en",
                subject = "Your ORD sign-in code",
                preheader = "Your ORD sign-in code is {{OTP_CODE}}. It expires in 10 minutes.",
                heading = "Enter your code",
                intro = "Enter the 6-digit code below to finish signing in to ORD.",
                buttonLabel = "Sign in with code",
                expiryNote = "The code is valid for 10 minutes",
                ignoreNote = "If you did not request this email, you can safely ignore it.",
                footerTagline = "© {{YEAR}} ORD · AI-powered language learning",
            )
            UiLocale.PL -> OtpEmailCopy(
                htmlLang = "pl",
                subject = "Twój kod logowania ORD",
                preheader = "Twój kod logowania ORD: {{OTP_CODE}}. Kod wygasa po 10 minutach.",
                heading = "Wprowadź swój kod",
                intro = "Wpisz 6-cyfrowy kod poniżej, aby dokończyć logowanie do ORD.",
                buttonLabel = "Zaloguj się kodem",
                expiryNote = "Kod jest ważny przez 10 minut",
                ignoreNote = "Jeśli to nie Ty prosiłeś o tę wiadomość, możesz ją zignorować.",
                footerTagline = "© {{YEAR}} ORD · Nauka języków wspierana przez AI",
            )
            UiLocale.DE -> OtpEmailCopy(
                htmlLang = "de",
                subject = "Ihr ORD-Anmeldecode",
                preheader = "Ihr ORD-Anmeldecode: {{OTP_CODE}}. Er läuft in 10 Minuten ab.",
                heading = "Geben Sie Ihren Code ein",
                intro = "Geben Sie unten den 6-stelligen Code ein, um die Anmeldung bei ORD abzuschließen.",
                buttonLabel = "Mit Code anmelden",
                expiryNote = "Der Code ist 10 Minuten gültig",
                ignoreNote = "Wenn Sie diese E-Mail nicht angefordert haben, können Sie sie ignorieren.",
                footerTagline = "© {{YEAR}} ORD · KI-gestütztes Sprachenlernen",
            )
        }
}
