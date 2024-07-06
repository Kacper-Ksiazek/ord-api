package com.backend.ord.seeders.entities;

import com.backend.ord.domain.entities.LanguageProficiency;
import com.backend.ord.domain.entities.User;
import com.backend.ord.enums.Language.LanguageName;
import com.backend.ord.repositories.LanguageProficiencyRepository;
import com.backend.ord.seeders.factories.LanguageProficiencyFactory;
import org.springframework.stereotype.Component;

@Component
public class LanguageProficiencySeeder implements SeederInterface<LanguageProficiency> {
    private final UserSeeder userSeeder;
    private final LanguageProficiencyRepository languageProficiencyRepository;
    private final LanguageProficiencyFactory languageProficiencyFactory;

    public LanguageProficiencySeeder(UserSeeder userSeeder, LanguageProficiencyRepository languageProficiencyRepository, LanguageProficiencyFactory languageProficiencyFactory) {
        this.userSeeder = userSeeder;
        this.languageProficiencyRepository = languageProficiencyRepository;
        this.languageProficiencyFactory = languageProficiencyFactory;
    }

    @Override
    public LanguageProficiency insertRow() {
        // Generate user to fill the foreign key constraint
        User user = userSeeder.insertRow();
        return insertRow(user);
    }

    @Override
    public void deleteAll() {
        languageProficiencyRepository.deleteAll();
    }

    public LanguageProficiency insertRow(User user) {
        LanguageProficiency data = languageProficiencyFactory.mockEntity(user);
        return languageProficiencyRepository.save(data);
    }

    public LanguageProficiency insertRow(User user, LanguageName language) {
        LanguageProficiency data = languageProficiencyFactory.mockEntity(user);
        data.setLanguage(language);

        return languageProficiencyRepository.save(data);
    }
}
