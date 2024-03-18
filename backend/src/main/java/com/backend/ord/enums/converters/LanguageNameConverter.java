package com.backend.ord.enums.converters;

import com.backend.ord.enums.LanguageName;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter
public class LanguageNameConverter implements AttributeConverter<LanguageName, String> {
    @Override
    public String convertToDatabaseColumn(LanguageName attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public LanguageName convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;

        return Stream.of(LanguageName.values())
                .filter(c -> c.toString().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
