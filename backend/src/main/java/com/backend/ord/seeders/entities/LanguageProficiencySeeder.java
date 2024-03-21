package com.backend.ord.seeders.entities;

import com.backend.ord.domain.entities.LanguageProficiency;
import com.backend.ord.domain.entities.User;
import com.backend.ord.enums.LanguageName;
import com.backend.ord.repositories.LanguageProficiencyRepository;
import com.backend.ord.seeders.factories.LanguageProficiencyFactory;
import org.springframework.stereotype.Component;

@Component
public class LanguageProficiencySeeder implements SeederInterface<LanguageProficiency> {
    private final LanguageProficiencyRepository languageProficiencyRepository;
    private final UserSeeder userSeeder;

    public LanguageProficiencySeeder(LanguageProficiencyRepository languageProficiencyRepository, UserSeeder userSeeder) {
        this.languageProficiencyRepository = languageProficiencyRepository;
        this.userSeeder = userSeeder;
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
        LanguageProficiency data = LanguageProficiencyFactory.mockEntity(user);
        return languageProficiencyRepository.save(data);
    }

    public LanguageProficiency insertRow(User user, LanguageName language) {
        LanguageProficiency data = LanguageProficiencyFactory.mockEntity(user);
        data.setLanguage(language);

        return languageProficiencyRepository.save(data);
    }
}
