package com.backend.ord.api.responses.openai;

import java.util.List;

class Usage {
    private int total_tokens;
    private int prompt_tokens;
    private int completion_tokens;

    Usage(int total_tokens, int prompt_tokens, int completion_tokens) {
        this.total_tokens = total_tokens;
        this.prompt_tokens = prompt_tokens;
        this.completion_tokens = completion_tokens;
    }

    public static UsageBuilder builder() {
        return new UsageBuilder();
    }

    public int getTotal_tokens() {
        return this.total_tokens;
    }

    public int getPrompt_tokens() {
        return this.prompt_tokens;
    }

    public int getCompletion_tokens() {
        return this.completion_tokens;
    }

    public void setTotal_tokens(int total_tokens) {
        this.total_tokens = total_tokens;
    }

    public void setPrompt_tokens(int prompt_tokens) {
        this.prompt_tokens = prompt_tokens;
    }

    public void setCompletion_tokens(int completion_tokens) {
        this.completion_tokens = completion_tokens;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Usage)) return false;
        final Usage other = (Usage) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getTotal_tokens() != other.getTotal_tokens()) return false;
        if (this.getPrompt_tokens() != other.getPrompt_tokens()) return false;
        if (this.getCompletion_tokens() != other.getCompletion_tokens()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Usage;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getTotal_tokens();
        result = result * PRIME + this.getPrompt_tokens();
        result = result * PRIME + this.getCompletion_tokens();
        return result;
    }

    public String toString() {
        return "Usage(total_tokens=" + this.getTotal_tokens() + ", prompt_tokens=" + this.getPrompt_tokens() + ", completion_tokens=" + this.getCompletion_tokens() + ")";
    }

    public static class UsageBuilder {
        private int total_tokens;
        private int prompt_tokens;
        private int completion_tokens;

        UsageBuilder() {
        }

        public UsageBuilder total_tokens(int total_tokens) {
            this.total_tokens = total_tokens;
            return this;
        }

        public UsageBuilder prompt_tokens(int prompt_tokens) {
            this.prompt_tokens = prompt_tokens;
            return this;
        }

        public UsageBuilder completion_tokens(int completion_tokens) {
            this.completion_tokens = completion_tokens;
            return this;
        }

        public Usage build() {
            return new Usage(this.total_tokens, this.prompt_tokens, this.completion_tokens);
        }

        public String toString() {
            return "Usage.UsageBuilder(total_tokens=" + this.total_tokens + ", prompt_tokens=" + this.prompt_tokens + ", completion_tokens=" + this.completion_tokens + ")";
        }
    }
}

public class OpenAIResponse {
    private String id;
    private String object;
    private int created;
    private String model;

    private List<ChatGPTChoice> choices;

    private Usage usage;

    private String system_fingerprint;

    OpenAIResponse(String id, String object, int created, String model, List<ChatGPTChoice> choices, Usage usage, String system_fingerprint) {
        this.id = id;
        this.object = object;
        this.created = created;
        this.model = model;
        this.choices = choices;
        this.usage = usage;
        this.system_fingerprint = system_fingerprint;
    }

    public static OpenAIResponseBuilder builder() {
        return new OpenAIResponseBuilder();
    }

    public String getActualResponse() {
        return choices.getFirst().getMessage().getContent();
    }

    public String getId() {
        return this.id;
    }

    public String getObject() {
        return this.object;
    }

    public int getCreated() {
        return this.created;
    }

    public String getModel() {
        return this.model;
    }

    public List<ChatGPTChoice> getChoices() {
        return this.choices;
    }

    public Usage getUsage() {
        return this.usage;
    }

    public String getSystem_fingerprint() {
        return this.system_fingerprint;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setChoices(List<ChatGPTChoice> choices) {
        this.choices = choices;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public void setSystem_fingerprint(String system_fingerprint) {
        this.system_fingerprint = system_fingerprint;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof OpenAIResponse)) return false;
        final OpenAIResponse other = (OpenAIResponse) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$object = this.getObject();
        final Object other$object = other.getObject();
        if (this$object == null ? other$object != null : !this$object.equals(other$object)) return false;
        if (this.getCreated() != other.getCreated()) return false;
        final Object this$model = this.getModel();
        final Object other$model = other.getModel();
        if (this$model == null ? other$model != null : !this$model.equals(other$model)) return false;
        final Object this$choices = this.getChoices();
        final Object other$choices = other.getChoices();
        if (this$choices == null ? other$choices != null : !this$choices.equals(other$choices)) return false;
        final Object this$usage = this.getUsage();
        final Object other$usage = other.getUsage();
        if (this$usage == null ? other$usage != null : !this$usage.equals(other$usage)) return false;
        final Object this$system_fingerprint = this.getSystem_fingerprint();
        final Object other$system_fingerprint = other.getSystem_fingerprint();
        if (this$system_fingerprint == null ? other$system_fingerprint != null : !this$system_fingerprint.equals(other$system_fingerprint))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof OpenAIResponse;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $object = this.getObject();
        result = result * PRIME + ($object == null ? 43 : $object.hashCode());
        result = result * PRIME + this.getCreated();
        final Object $model = this.getModel();
        result = result * PRIME + ($model == null ? 43 : $model.hashCode());
        final Object $choices = this.getChoices();
        result = result * PRIME + ($choices == null ? 43 : $choices.hashCode());
        final Object $usage = this.getUsage();
        result = result * PRIME + ($usage == null ? 43 : $usage.hashCode());
        final Object $system_fingerprint = this.getSystem_fingerprint();
        result = result * PRIME + ($system_fingerprint == null ? 43 : $system_fingerprint.hashCode());
        return result;
    }

    public String toString() {
        return "OpenAIResponse(id=" + this.getId() + ", object=" + this.getObject() + ", created=" + this.getCreated() + ", model=" + this.getModel() + ", choices=" + this.getChoices() + ", usage=" + this.getUsage() + ", system_fingerprint=" + this.getSystem_fingerprint() + ")";
    }

    public static class OpenAIResponseBuilder {
        private String id;
        private String object;
        private int created;
        private String model;
        private List<ChatGPTChoice> choices;
        private Usage usage;
        private String system_fingerprint;

        OpenAIResponseBuilder() {
        }

        public OpenAIResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public OpenAIResponseBuilder object(String object) {
            this.object = object;
            return this;
        }

        public OpenAIResponseBuilder created(int created) {
            this.created = created;
            return this;
        }

        public OpenAIResponseBuilder model(String model) {
            this.model = model;
            return this;
        }

        public OpenAIResponseBuilder choices(List<ChatGPTChoice> choices) {
            this.choices = choices;
            return this;
        }

        public OpenAIResponseBuilder usage(Usage usage) {
            this.usage = usage;
            return this;
        }

        public OpenAIResponseBuilder system_fingerprint(String system_fingerprint) {
            this.system_fingerprint = system_fingerprint;
            return this;
        }

        public OpenAIResponse build() {
            return new OpenAIResponse(this.id, this.object, this.created, this.model, this.choices, this.usage, this.system_fingerprint);
        }

        public String toString() {
            return "OpenAIResponse.OpenAIResponseBuilder(id=" + this.id + ", object=" + this.object + ", created=" + this.created + ", model=" + this.model + ", choices=" + this.choices + ", usage=" + this.usage + ", system_fingerprint=" + this.system_fingerprint + ")";
        }
    }
}
