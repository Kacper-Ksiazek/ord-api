package com.backend.ord.enums.converters;

import com.backend.ord.enums.LanguageProficiencyLevel;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter
public class LanguageProficiencyLevelConverter implements AttributeConverter<LanguageProficiencyLevel, String> {
    @Override
    public String convertToDatabaseColumn(LanguageProficiencyLevel attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public LanguageProficiencyLevel convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;

        return Stream.of(LanguageProficiencyLevel.values())
                .filter(c -> c.toString().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
