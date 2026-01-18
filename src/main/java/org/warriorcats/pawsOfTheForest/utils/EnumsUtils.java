package org.warriorcats.pawsOfTheForest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for working with Java enums.
 * 
 * <p>This class provides helper methods for enum operations, particularly
 * for flexible enum parsing and conversion from strings. It includes methods
 * that allow partial matching and case-insensitive enum lookups.</p>
 * 
 * <p>The class is designed as a utility class with static methods only and
 * cannot be instantiated.</p>
 * 
 * @author PawsOfTheForest Team
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class EnumsUtils {

    /**
     * Converts a string to an enum value with flexible matching.
     * 
     * <p>This method first attempts to find an enum constant whose string
     * representation starts with the given string (case-insensitive). If no
     * partial match is found, it falls back to exact enum value matching.</p>
     * 
     * <p>This is useful for user input where partial enum names should be
     * accepted (e.g., "for" matching "FOREST" enum value).</p>
     * 
     * @param <T> the enum type
     * @param str the string to convert to enum
     * @param enumClass the class of the enum type
     * @return the matching enum constant
     * @throws IllegalArgumentException if no matching enum constant is found
     * 
     * @example
     * <pre>
     * // Assuming an enum: enum Color { RED, GREEN, BLUE }
     * Color color1 = EnumsUtils.from("re", Color.class);  // Returns Color.RED
     * Color color2 = EnumsUtils.from("RED", Color.class); // Returns Color.RED
     * </pre>
     */
    public static <T extends Enum<T>> T from(String str, Class<T> enumClass) {
        // First try partial matching (case-insensitive prefix search)
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.toString().toLowerCase().startsWith(str.toLowerCase())) {
                return constant;
            }
        }
        // Fall back to exact matching if no partial match found
        return Enum.valueOf(enumClass, str);
    }
}
