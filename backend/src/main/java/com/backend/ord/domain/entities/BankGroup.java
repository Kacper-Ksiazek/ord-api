package com.backend.ord.domain.entities;

import com.backend.ord.domain.entities.abstracts.EntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "bank_groups")
public class BankGroup extends EntityBase {
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "color", nullable = false, length = 6)
    private String color;

    public BankGroup(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public BankGroup() {
    }

    public static BankGroupBuilder builder() {
        return new BankGroupBuilder();
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

    public static class BankGroupBuilder {
        private String name;
        private String color;

        BankGroupBuilder() {
        }

        public BankGroupBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BankGroupBuilder color(String color) {
            this.color = color;
            return this;
        }

        public BankGroup build() {
            return new BankGroup(this.name, this.color);
        }

        public String toString() {
            return "BankGroup.BankGroupBuilder(name=" + this.name + ", color=" + this.color + ")";
        }
    }
}