package com.backend.ord.api.requests.openai;

public class ChatGPTMessage {
    private ChatGPTRole role;
    private String content;

    ChatGPTMessage(ChatGPTRole role, String content) {
        this.role = role;
        this.content = content;
    }

    public static ChatGPTMessageBuilder builder() {
        return new ChatGPTMessageBuilder();
    }

    public ChatGPTRole getRole() {
        return this.role;
    }

    public String getContent() {
        return this.content;
    }

    public void setRole(ChatGPTRole role) {
        this.role = role;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ChatGPTMessage)) return false;
        final ChatGPTMessage other = (ChatGPTMessage) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$role = this.getRole();
        final Object other$role = other.getRole();
        if (this$role == null ? other$role != null : !this$role.equals(other$role)) return false;
        final Object this$content = this.getContent();
        final Object other$content = other.getContent();
        if (this$content == null ? other$content != null : !this$content.equals(other$content)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ChatGPTMessage;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $role = this.getRole();
        result = result * PRIME + ($role == null ? 43 : $role.hashCode());
        final Object $content = this.getContent();
        result = result * PRIME + ($content == null ? 43 : $content.hashCode());
        return result;
    }

    public String toString() {
        return "ChatGPTMessage(role=" + this.getRole() + ", content=" + this.getContent() + ")";
    }

    public static class ChatGPTMessageBuilder {
        private ChatGPTRole role;
        private String content;

        ChatGPTMessageBuilder() {
        }

        public ChatGPTMessageBuilder role(ChatGPTRole role) {
            this.role = role;
            return this;
        }

        public ChatGPTMessageBuilder content(String content) {
            this.content = content;
            return this;
        }

        public ChatGPTMessage build() {
            return new ChatGPTMessage(this.role, this.content);
        }

        public String toString() {
            return "ChatGPTMessage.ChatGPTMessageBuilder(role=" + this.role + ", content=" + this.content + ")";
        }
    }
}
