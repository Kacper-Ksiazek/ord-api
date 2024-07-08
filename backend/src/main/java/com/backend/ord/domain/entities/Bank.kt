package com.backend.ord.domain.entities;

import com.backend.ord.domain.entities.abstracts.EntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "banks")
public class Bank extends EntityBase {
    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "group_id")
    private BankGroup group;

    public Bank(@Size(max = 64) @NotNull String name, @Size(max = 255) @NotNull String description, @NotNull User user, BankGroup group) {
        this.name = name;
        this.description = description;
        this.user = user;
        this.group = group;
    }

    public Bank() {
    }

    public static BankBuilder builder() {
        return new BankBuilder();
    }

    public @Size(max = 64) @NotNull String getName() {
        return this.name;
    }

    public @Size(max = 255) @NotNull String getDescription() {
        return this.description;
    }

    public @NotNull User getUser() {
        return this.user;
    }

    public BankGroup getGroup() {
        return this.group;
    }

    public void setName(@Size(max = 64) @NotNull String name) {
        this.name = name;
    }

    public void setDescription(@Size(max = 255) @NotNull String description) {
        this.description = description;
    }

    public void setUser(@NotNull User user) {
        this.user = user;
    }

    public void setGroup(BankGroup group) {
        this.group = group;
    }

    public static class BankBuilder {
        private @Size(max = 64) @NotNull String name;
        private @Size(max = 255) @NotNull String description;
        private @NotNull User user;
        private BankGroup group;

        BankBuilder() {
        }

        public BankBuilder name(@Size(max = 64) @NotNull String name) {
            this.name = name;
            return this;
        }

        public BankBuilder description(@Size(max = 255) @NotNull String description) {
            this.description = description;
            return this;
        }

        public BankBuilder user(@NotNull User user) {
            this.user = user;
            return this;
        }

        public BankBuilder group(BankGroup group) {
            this.group = group;
            return this;
        }

        public Bank build() {
            return new Bank(this.name, this.description, this.user, this.group);
        }

        public String toString() {
            return "Bank.BankBuilder(name=" + this.name + ", description=" + this.description + ", user=" + this.user + ", group=" + this.group + ")";
        }
    }
}