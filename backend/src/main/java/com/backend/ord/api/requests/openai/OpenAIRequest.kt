package com.backend.ord.api.requests.openai

class OpenAIRequest(
    /**
     * The model to use for the request (e.g. "gpt-3.5-turbo" or "gpt-4o").
     */
    private var model: String?,

    /**
     * The prompt to use for the request.
     */
    private var messages: Array<ChatGPTMessage>,

    /**
     * The temperature ( value between 0 and 1 used to control the randomness of the output, where 0 is deterministic and 1 is maximum randomness, usually around 0.5) to use for the request.
     */
    private var temperature: Float,

    /**
     * The maximum number of tokens to generate for the request.
     */
    private var max_tokens: Int,

    /**
     * The number of completions to generate for the request. It means how many different completions you want to generate for the same prompt.
     */
    private val top_p: Int = 1,

    /**
     *This parameter is used to discourage the model from repeating the same words or phrases too frequently within the generated text. It is a value that is added to the log-probability of a token each time it occurs in the generated text. A higher frequency_penalty value will result in the model being more conservative in its use of repeated tokens. A frequency_penalty of 0.0 will not modify the log-probabilities. The default value is 0.0 and it can be any positive value, with 0.0 meaning no penalty and 1.0 meaning the model will be very conservative in its use of repeated tokens.
     */
    private val frequency_penalty: Int = 0,

    /**
     * This parameter is used to encourage the model to include a diverse range of tokens in the generated text. It is a value that is subtracted from the log-probability of a token each time it is generated. A higher presence_penalty value will result in the model being more likely to generate tokens that have not yet been included in the generated text. A presence_penalty of 0.0 will not modify the log-probabilities. The default value is 0.0 and it can be any positive value, with 0.0 meaning no penalty.
     */
    private val presence_penalty: Int = 0,
) {
}
