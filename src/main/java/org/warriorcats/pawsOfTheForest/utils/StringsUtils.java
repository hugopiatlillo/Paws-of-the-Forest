package org.warriorcats.pawsOfTheForest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for string manipulation and formatting operations.
 * 
 * <p>This class provides common string processing utilities including
 * capitalization, formatting, and text transformation methods. It focuses
 * on text presentation and user-friendly string formatting for display
 * purposes.</p>
 * 
 * <p>The class is designed as a utility class with static methods only and
 * cannot be instantiated.</p>
 * 
 * @author PawsOfTheForest Team
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class StringsUtils {

    /**
     * Capitalizes the first character of a string.
     * 
     * <p>This method converts the first character to uppercase while leaving
     * the rest of the string unchanged. Null and empty strings are returned
     * as-is without modification.</p>
     * 
     * @param input the string to capitalize
     * @return the capitalized string, or the original input if null/empty
     * 
     * @example
     * <pre>
     * StringsUtils.capitalize("hello");    // Returns "Hello"
     * StringsUtils.capitalize("WORLD");    // Returns "WORLD"
     * StringsUtils.capitalize("");         // Returns ""
     * StringsUtils.capitalize(null);       // Returns null
     * </pre>
     */
    public static String capitalize(String input) {
        // Handle null and empty strings
        if (input == null || input.isEmpty()) {
            return input;
        }
        // Capitalize first character and append the rest
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * Capitalizes each word in a string after splitting by a delimiter and joins with spaces.
     * 
     * <p>This method splits the input string by the specified delimiter, converts each
     * part to lowercase, capitalizes the first letter of each part, and joins them
     * with single spaces. This is useful for converting enum constants or
     * underscore-separated strings into human-readable format.</p>
     * 
     * @param input the string to process
     * @param splitSymbol the delimiter to split the string on (e.g., "_", "-")
     * @return the formatted string with capitalized words separated by spaces
     * 
     * @example
     * <pre>
     * StringsUtils.capitalizeWithSpaces("HELLO_WORLD", "_");     // Returns "Hello World"
     * StringsUtils.capitalizeWithSpaces("first-second", "-");    // Returns "First Second"
     * StringsUtils.capitalizeWithSpaces("oneword", "_");         // Returns "Oneword"
     * </pre>
     */
    public static String capitalizeWithSpaces(String input, String splitSymbol) {
        StringBuilder builder = new StringBuilder();
        // Split input by the delimiter and convert to lowercase
        String[] split = input.toLowerCase().split(splitSymbol);
        for (String stub : split) {
            // Capitalize each part and add to result
            builder.append(StringsUtils.capitalize(stub));
            builder.append(" ");
        }
        // Remove trailing space
        return builder.toString().trim();
    }
}
