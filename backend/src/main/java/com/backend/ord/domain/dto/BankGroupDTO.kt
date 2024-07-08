package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;

public class BankGroupDTO extends DTOBase {
    private String name;
    private String color;

    public BankGroupDTO(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public BankGroupDTO() {
    }

    public static BankGroupDTOBuilder builder() {
        return new BankGroupDTOBuilder();
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String toString() {
        return "BankGroupDTO(name=" + this.getName() + ", color=" + this.getColor() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BankGroupDTO)) return false;
        final BankGroupDTO other = (BankGroupDTO) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$color = this.getColor();
        final Object other$color = other.getColor();
        if (this$color == null ? other$color != null : !this$color.equals(other$color)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BankGroupDTO;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $color = this.getColor();
        result = result * PRIME + ($color == null ? 43 : $color.hashCode());
        return result;
    }

    public static class BankGroupDTOBuilder {
        private String name;
        private String color;

        BankGroupDTOBuilder() {
        }

        public BankGroupDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BankGroupDTOBuilder color(String color) {
            this.color = color;
            return this;
        }

        public BankGroupDTO build() {
            return new BankGroupDTO(this.name, this.color);
        }

        public String toString() {
            return "BankGroupDTO.BankGroupDTOBuilder(name=" + this.name + ", color=" + this.color + ")";
        }
    }
}
