package org.swiften.javautilities.number;

/**
 * Created by haipham on 4/9/17.
 */
public final class NumberUtil {
    /**
     * Check if a number is even.
     * @param number {@link Integer} value.
     * @return {@link Boolean} value.
     */
    public static boolean isEven(int number) {
        return number % 2 == 0;
    }

    /**
     * Check if a number is odd.
     * @param number {@link Integer} value.
     * @return {@link Boolean} value.
     * @see #isEven(int)
     */
    public static boolean isOdd(int number) {
        return !isEven(number);
    }

    private NumberUtil() {}
}
