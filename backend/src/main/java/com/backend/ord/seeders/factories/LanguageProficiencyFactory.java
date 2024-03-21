package com.backend.ord.seeders.factories;

import com.backend.ord.domain.entities.LanguageProficiency;
import com.backend.ord.domain.entities.User;
import com.backend.ord.enums.LanguageName;
import com.backend.ord.enums.LanguageProficiencyLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LanguageProficiencyFactory extends AbstractFactory {
    public static LanguageName mockLanguageName() {
        return LanguageName.values()[faker.random().nextInt(LanguageName.values().length)];
    }

    public static LanguageProficiencyLevel mockProficiencyLevel() {
        return LanguageProficiencyLevel.values()[faker.random().nextInt(LanguageProficiencyLevel.values().length)];
    }

    public static List<LanguageName> mockUniqueLanguages(int N) {
        return FactoryUtils.getNRandomUniqueValuesFromEnum(LanguageName.class, N);
    }

    public static LanguageProficiency mockEntity(User user) {
        // Validate received user
        if (user == null || user.getId() == null)
            throw new IllegalArgumentException("User must be provided with a valid ID");

        // Return a new language proficiency entity
        return LanguageProficiency.builder()
                .language(mockLanguageName())
                .proficiency(mockProficiencyLevel())
                .user(user)
                .build();
    }

    public static LanguageProficiency mockEntity() {
        // Return a new language proficiency entity
        return LanguageProficiency.builder()
                .language(mockLanguageName())
                .proficiency(mockProficiencyLevel())
                .user(null)
                .build();
    }
}
