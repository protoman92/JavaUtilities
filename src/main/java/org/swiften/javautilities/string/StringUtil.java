package org.swiften.javautilities.string;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.number.NumberUtil;
import org.swiften.javautilities.object.ObjectUtil;

import java.util.List;

/**
 * Created by haipham on 4/6/17.
 */
public final class StringUtil {
    /**
     * Check if {@link String} is not null and not empty.
     * @param text {@link String} value.
     * @return {@link Boolean} value.
     */
    public static boolean isNotNullOrEmpty(@Nullable String text) {
        return ObjectUtil.nonNull(text) && !text.isEmpty();
    }

    /**
     * Check if {@link String} is either null or empty.
     * @param text {@link String} value.
     * @return {@link Boolean} value.
     */
    public static boolean isNullOrEmpty(@Nullable String text) {
        return ObjectUtil.isNull(text) || text.isEmpty();
    }

    /**
     * Get a {@link String} with a random length.
     * @param length {@link Integer} value.
     * @return {@link String} value.
     * @see NumberUtil#randomBetween(int, int)
     */
    @NotNull
    public static String randomString(int length) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char character = (char)NumberUtil.randomBetween(97, 123);
            builder.append(character);
        }

        return builder.toString();
    }

    /**
     * Get a {@link String} of random digits.
     * @param length {@link Integer} value.
     * @return {@link String} value.
     * @see NumberUtil#randomDigits(int)
     */
    @NotNull
    public static String randomDigitString(int length) {
        List<Integer> numbers = NumberUtil.randomDigits(length);
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append(numbers.get(i));
        }

        return builder.toString();
    }

    private StringUtil() {}
}
