package com.backend.ord.api.responses.openai;

import com.backend.ord.api.requests.openai.ChatGPTMessage;

public class ChatGPTChoice {
    private int index;
    private ChatGPTMessage message;
    private Float logprobs;
    private String finish_reason;

    ChatGPTChoice(int index, ChatGPTMessage message, Float logprobs, String finish_reason) {
        this.index = index;
        this.message = message;
        this.logprobs = logprobs;
        this.finish_reason = finish_reason;
    }

    public static ChatGPTChoiceBuilder builder() {
        return new ChatGPTChoiceBuilder();
    }

    public int getIndex() {
        return this.index;
    }

    public ChatGPTMessage getMessage() {
        return this.message;
    }

    public Float getLogprobs() {
        return this.logprobs;
    }

    public String getFinish_reason() {
        return this.finish_reason;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setMessage(ChatGPTMessage message) {
        this.message = message;
    }

    public void setLogprobs(Float logprobs) {
        this.logprobs = logprobs;
    }

    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ChatGPTChoice)) return false;
        final ChatGPTChoice other = (ChatGPTChoice) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getIndex() != other.getIndex()) return false;
        final Object this$message = this.getMessage();
        final Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) return false;
        final Object this$logprobs = this.getLogprobs();
        final Object other$logprobs = other.getLogprobs();
        if (this$logprobs == null ? other$logprobs != null : !this$logprobs.equals(other$logprobs)) return false;
        final Object this$finish_reason = this.getFinish_reason();
        final Object other$finish_reason = other.getFinish_reason();
        if (this$finish_reason == null ? other$finish_reason != null : !this$finish_reason.equals(other$finish_reason))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ChatGPTChoice;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getIndex();
        final Object $message = this.getMessage();
        result = result * PRIME + ($message == null ? 43 : $message.hashCode());
        final Object $logprobs = this.getLogprobs();
        result = result * PRIME + ($logprobs == null ? 43 : $logprobs.hashCode());
        final Object $finish_reason = this.getFinish_reason();
        result = result * PRIME + ($finish_reason == null ? 43 : $finish_reason.hashCode());
        return result;
    }

    public String toString() {
        return "ChatGPTChoice(index=" + this.getIndex() + ", message=" + this.getMessage() + ", logprobs=" + this.getLogprobs() + ", finish_reason=" + this.getFinish_reason() + ")";
    }

    public static class ChatGPTChoiceBuilder {
        private int index;
        private ChatGPTMessage message;
        private Float logprobs;
        private String finish_reason;

        ChatGPTChoiceBuilder() {
        }

        public ChatGPTChoiceBuilder index(int index) {
            this.index = index;
            return this;
        }

        public ChatGPTChoiceBuilder message(ChatGPTMessage message) {
            this.message = message;
            return this;
        }

        public ChatGPTChoiceBuilder logprobs(Float logprobs) {
            this.logprobs = logprobs;
            return this;
        }

        public ChatGPTChoiceBuilder finish_reason(String finish_reason) {
            this.finish_reason = finish_reason;
            return this;
        }

        public ChatGPTChoice build() {
            return new ChatGPTChoice(this.index, this.message, this.logprobs, this.finish_reason);
        }

        public String toString() {
            return "ChatGPTChoice.ChatGPTChoiceBuilder(index=" + this.index + ", message=" + this.message + ", logprobs=" + this.logprobs + ", finish_reason=" + this.finish_reason + ")";
        }
    }
}
