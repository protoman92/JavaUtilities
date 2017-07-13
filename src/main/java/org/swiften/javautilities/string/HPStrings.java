package org.swiften.javautilities.string;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.number.HPNumbers;
import org.swiften.javautilities.object.HPObjects;

import java.util.List;

/**
 * Created by haipham on 4/6/17.
 */
public final class HPStrings {
    /**
     * Check if {@link String} is not null and not empty.
     * @param text {@link String} value.
     * @return {@link Boolean} value.
     */
    public static boolean isNotNullOrEmpty(@Nullable String text) {
        return HPObjects.nonNull(text) && !text.isEmpty();
    }

    /**
     * Check if {@link String} is either null or empty.
     * @param text {@link String} value.
     * @return {@link Boolean} value.
     */
    public static boolean isNullOrEmpty(@Nullable String text) {
        return HPObjects.isNull(text) || text.isEmpty();
    }

    /**
     * Get a {@link String} with a random length.
     * @param length {@link Integer} value.
     * @return {@link String} value.
     * @see HPNumbers#randomBetween(int, int)
     */
    @NotNull
    public static String randomString(int length) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char character = (char) HPNumbers.randomBetween(97, 123);
            builder.append(character);
        }

        return builder.toString();
    }

    /**
     * Get a {@link String} of random digits.
     * @param length {@link Integer} value.
     * @return {@link String} value.
     * @see HPNumbers#randomDigits(int)
     */
    @NotNull
    public static String randomDigitString(int length) {
        List<Integer> numbers = HPNumbers.randomDigits(length);
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append(numbers.get(i));
        }

        return builder.toString();
    }

    /**
     * Check if a {@link String} is not null/not empty, and throw
     * {@link RuntimeException} otherwise.
     * @param target {@link String} value.
     * @param message {@link String} value.
     * @see #isNullOrEmpty(String)
     */
    public static void requireNotNullOrEmpty(@NotNull String target,
                                             @NotNull String message) {
        if (isNullOrEmpty(target)) {
            throw new RuntimeException(message);
        }
    }

    /**
     * Same as above, but uses a default message.
     * @param target {@link String} value.
     * @see #requireNotNullOrEmpty(String, String)
     */
    public static void requireNotNullOrEmpty(@NotNull String target) {
        requireNotNullOrEmpty(target, "");
    }

    /**
     * Remove all instance of {@link String} value from another {@link String}.
     * @param original The original {@link String} value.
     * @param removable The {@link String} value to be removed.
     * @return {@link String} value.
     */
    @NotNull
    public static String removeAll(@NotNull String original,
                                   @NotNull String removable) {
        return original.replaceAll(removable, "");
    }

    private HPStrings() {}
}
