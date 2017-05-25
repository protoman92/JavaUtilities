package org.swiften.javautilities.string;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.number.NumberTestUtil;

import java.util.List;

/**
 * Created by haipham on 5/25/17.
 */
public final class StringTestUtil {
    /**
     * Get a {@link String} with a random length.
     * @param length {@link Integer} value.
     * @return {@link String} value.
     * @see NumberTestUtil#randomBetween(int, int)
     */
    @NotNull
    public static String randomString(int length) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char character = (char)NumberTestUtil.randomBetween(97, 123);
            builder.append(character);
        }

        return builder.toString();
    }

    /**
     * Get a {@link String} of random digits.
     * @param length {@link Integer} value.
     * @return {@link String} value.
     * @see NumberTestUtil#randomDigits(int)
     */
    @NotNull
    public static String randomDigitString(int length) {
        List<Integer> numbers = NumberTestUtil.randomDigits(length);
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append(numbers.get(i));
        }

        return builder.toString();
    }

    private StringTestUtil() {}
}
