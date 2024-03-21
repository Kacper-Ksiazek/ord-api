package com.backend.ord.seeders.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FactoryUtils {
    private FactoryUtils() {
    }

    public static <T extends Enum<T>> List<T> getNRandomUniqueValuesFromEnum(Class<T> enumClass, int N) {
        // Ensure the class is an enum
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException("Class must be an enum");
        }

        // Get the values of the enum
        T[] enumConstants = enumClass.getEnumConstants();

        // Validate N
        if (N < 1 || N > enumConstants.length) {
            throw new IllegalArgumentException("N must be greater than 0 and less than or equal to the number of elements in the enum");
        }

        // Create a collection of unique values
        List<T> uniqueValues = new ArrayList<>();
        Collections.addAll(uniqueValues, enumConstants);

        // Shuffle the collection
        Collections.shuffle(uniqueValues);

        // Return a sublist of the first N elements
        return uniqueValues.subList(0, N);
    }

}
