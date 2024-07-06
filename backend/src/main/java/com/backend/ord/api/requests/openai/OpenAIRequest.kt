package com.backend.ord.api.requests.openai;

public class OpenAIRequest {
    private String model;
    private ChatGPTMessage[] messages;
    private float temperature;
    private int max_tokens;

    private final int top_p = 1;
    private final int frequency_penalty = 0;
    private final int presence_penalty = 0;

    OpenAIRequest(String model, ChatGPTMessage[] messages, float temperature, int max_tokens) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.max_tokens = max_tokens;
    }

    public static OpenAIRequestBuilder builder() {
        return new OpenAIRequestBuilder();
    }

    public String getModel() {
        return this.model;
    }

    public ChatGPTMessage[] getMessages() {
        return this.messages;
    }

    public float getTemperature() {
        return this.temperature;
    }

    public int getMax_tokens() {
        return this.max_tokens;
    }

    public int getTop_p() {
        return this.top_p;
    }

    public int getFrequency_penalty() {
        return this.frequency_penalty;
    }

    public int getPresence_penalty() {
        return this.presence_penalty;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setMessages(ChatGPTMessage[] messages) {
        this.messages = messages;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void setMax_tokens(int max_tokens) {
        this.max_tokens = max_tokens;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof OpenAIRequest)) return false;
        final OpenAIRequest other = (OpenAIRequest) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$model = this.getModel();
        final Object other$model = other.getModel();
        if (this$model == null ? other$model != null : !this$model.equals(other$model)) return false;
        if (!java.util.Arrays.deepEquals(this.getMessages(), other.getMessages())) return false;
        if (Float.compare(this.getTemperature(), other.getTemperature()) != 0) return false;
        if (this.getMax_tokens() != other.getMax_tokens()) return false;
        if (this.getTop_p() != other.getTop_p()) return false;
        if (this.getFrequency_penalty() != other.getFrequency_penalty()) return false;
        if (this.getPresence_penalty() != other.getPresence_penalty()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof OpenAIRequest;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $model = this.getModel();
        result = result * PRIME + ($model == null ? 43 : $model.hashCode());
        result = result * PRIME + java.util.Arrays.deepHashCode(this.getMessages());
        result = result * PRIME + Float.floatToIntBits(this.getTemperature());
        result = result * PRIME + this.getMax_tokens();
        result = result * PRIME + this.getTop_p();
        result = result * PRIME + this.getFrequency_penalty();
        result = result * PRIME + this.getPresence_penalty();
        return result;
    }

    public String toString() {
        return "OpenAIRequest(model=" + this.getModel() + ", messages=" + java.util.Arrays.deepToString(this.getMessages()) + ", temperature=" + this.getTemperature() + ", max_tokens=" + this.getMax_tokens() + ", top_p=" + this.getTop_p() + ", frequency_penalty=" + this.getFrequency_penalty() + ", presence_penalty=" + this.getPresence_penalty() + ")";
    }

    public static class OpenAIRequestBuilder {
        private String model;
        private ChatGPTMessage[] messages;
        private float temperature;
        private int max_tokens;

        OpenAIRequestBuilder() {
        }

        public OpenAIRequestBuilder model(String model) {
            this.model = model;
            return this;
        }

        public OpenAIRequestBuilder messages(ChatGPTMessage[] messages) {
            this.messages = messages;
            return this;
        }

        public OpenAIRequestBuilder temperature(float temperature) {
            this.temperature = temperature;
            return this;
        }

        public OpenAIRequestBuilder max_tokens(int max_tokens) {
            this.max_tokens = max_tokens;
            return this;
        }

        public OpenAIRequest build() {
            return new OpenAIRequest(this.model, this.messages, this.temperature, this.max_tokens);
        }

        public String toString() {
            return "OpenAIRequest.OpenAIRequestBuilder(model=" + this.model + ", messages=" + java.util.Arrays.deepToString(this.messages) + ", temperature=" + this.temperature + ", max_tokens=" + this.max_tokens + ")";
        }
    }
}
