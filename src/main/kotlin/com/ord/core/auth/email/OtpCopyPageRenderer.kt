package com.ord.core.auth.email

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object OtpCopyPageRenderer {
    fun render(code: String, loginUrl: String): String {
        val jsCode = code.replace("\\", "\\\\").replace("'", "\\'")
        val continueUrl = buildContinueUrl(loginUrl, code)
        val continueHref = escapeHtmlAttribute(continueUrl)

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Copy ORD sign-in code</title>
                <style>
                    body {
                        margin: 0;
                        min-height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-family: 'Noto Sans', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                        background: #f6f4ef;
                        color: #1c1b18;
                    }
                    main {
                        width: min(100%, 360px);
                        margin: 24px;
                        padding: 32px 28px;
                        border: 1px solid #e7e4dc;
                        border-radius: 10px;
                        background: #ffffff;
                        text-align: center;
                    }
                    h1 { margin: 0 0 8px; font-size: 24px; font-weight: 500; }
                    p { margin: 0; color: #6b6860; font-size: 15px; line-height: 22px; }
                    .code {
                        margin: 20px 0 16px;
                        font-size: 32px;
                        font-weight: 500;
                        letter-spacing: 0.2em;
                        font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
                    }
                    a.button {
                        display: inline-block;
                        margin-top: 20px;
                        padding: 12px 20px;
                        border-radius: 10px;
                        background: #1c1b18;
                        color: #f6f4ef;
                        text-decoration: none;
                        font-size: 15px;
                        font-weight: 500;
                    }
                    #status { margin-top: 16px; min-height: 22px; color: #3a3832; font-size: 14px; }
                </style>
            </head>
            <body>
                <main>
                    <h1>Sign-in code copied</h1>
                    <p id="status">Copying code to your clipboard…</p>
                    <div class="code" aria-label="OTP code">$code</div>
                    <p>Paste it on the ORD sign-in screen. The code expires in 10 minutes.</p>
                    <a class="button" href="$continueHref">Continue to sign in</a>
                </main>
                <script>
                    (function () {
                        var code = '$jsCode';
                        var status = document.getElementById('status');
                        function setStatus(message) {
                            if (status) status.textContent = message;
                        }
                        if (!navigator.clipboard || !navigator.clipboard.writeText) {
                            setStatus('Select the code above and copy it manually.');
                            return;
                        }
                        navigator.clipboard.writeText(code).then(function () {
                            setStatus('Code copied to your clipboard.');
                        }).catch(function () {
                            setStatus('Select the code above and copy it manually.');
                        });
                    })();
                </script>
            </body>
            </html>
            """.trimIndent()
    }

    private fun buildContinueUrl(loginUrl: String, code: String): String {
        val base = loginUrl.trimEnd('/')
        val encodedCode = URLEncoder.encode(code, StandardCharsets.UTF_8)
        val separator = if (base.contains('?')) '&' else '?'
        return "$base$separator" + "otp=$encodedCode"
    }

    private fun escapeHtmlAttribute(value: String): String =
        value
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
}
