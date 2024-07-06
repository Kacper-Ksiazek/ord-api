package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;

public class BankDTO extends DTOBase {
    private String name;
    private String description;

    private UserDTO user;
    private BankGroupDTO group;

    public BankDTO(String name, String description, UserDTO user, BankGroupDTO group) {
        this.name = name;
        this.description = description;
        this.user = user;
        this.group = group;
    }

    public BankDTO() {
    }

    public static BankDTOBuilder builder() {
        return new BankDTOBuilder();
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public UserDTO getUser() {
        return this.user;
    }

    public BankGroupDTO getGroup() {
        return this.group;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public void setGroup(BankGroupDTO group) {
        this.group = group;
    }

    public String toString() {
        return "BankDTO(name=" + this.getName() + ", description=" + this.getDescription() + ", user=" + this.getUser() + ", group=" + this.getGroup() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BankDTO)) return false;
        final BankDTO other = (BankDTO) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        final Object this$user = this.getUser();
        final Object other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
        final Object this$group = this.getGroup();
        final Object other$group = other.getGroup();
        if (this$group == null ? other$group != null : !this$group.equals(other$group)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BankDTO;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $user = this.getUser();
        result = result * PRIME + ($user == null ? 43 : $user.hashCode());
        final Object $group = this.getGroup();
        result = result * PRIME + ($group == null ? 43 : $group.hashCode());
        return result;
    }

    public static class BankDTOBuilder {
        private String name;
        private String description;
        private UserDTO user;
        private BankGroupDTO group;

        BankDTOBuilder() {
        }

        public BankDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BankDTOBuilder description(String description) {
            this.description = description;
            return this;
        }

        public BankDTOBuilder user(UserDTO user) {
            this.user = user;
            return this;
        }

        public BankDTOBuilder group(BankGroupDTO group) {
            this.group = group;
            return this;
        }

        public BankDTO build() {
            return new BankDTO(this.name, this.description, this.user, this.group);
        }

        public String toString() {
            return "BankDTO.BankDTOBuilder(name=" + this.name + ", description=" + this.description + ", user=" + this.user + ", group=" + this.group + ")";
        }
    }
}
