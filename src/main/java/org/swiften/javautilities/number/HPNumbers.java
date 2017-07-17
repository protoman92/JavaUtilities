package org.swiften.javautilities.number;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by haipham on 4/9/17.
 */
public final class HPNumbers {
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

    /**
     * Inverse a number.
     * @param number {@link Number} instance.
     * @return {@link Double} value.
     */
    public static double inverse(@NotNull Number number) {
        return 1 / (number.doubleValue());
    }

    /**
     * Check if {@link Number} is larger than 0.
     * @param number {@link Number} instance.
     * @return {@link Boolean} value.
     */
    public static boolean largerThanZero(@NotNull Number number) {
        return number.doubleValue() > 0;
    }

    /**
     * Check if {@link Number} is 0.
     * @param number {@link Number} instance.
     * @return {@link Boolean} value.
     */
    public static boolean isZero(@NotNull Number number) {
        return number.doubleValue() == 0;
    }

    /**
     * Check if {@link Number} is less than 0.
     * @param number {@link Number} instance.
     * @return {@link Boolean} value.
     */
    public static boolean lessThanZero(@NotNull Number number) {
        return number.doubleValue() < 0;
    }

    /**
     * Produce a random number between two bounds.
     * @param from The inclusive lower bound.
     * @param to The non-inclusive upper bound.
     * @return {@link Integer} value.
     */
    public static int randomBetween(int from, int to) {
        return new Random().nextInt(to - from) + from;
    }

    /**
     * Get a {@link List} of random {@link Integer} digits.
     * @param length {@link Integer} value.
     * @return {@link List} of {@link Integer} values.
     */
    @NotNull
    public static List<Integer> randomDigits(int length) {
        List<Integer> numbers = new LinkedList<Integer>();
        Random rand = new Random();

        for (int i = 0; i < length; i++) {
            int number = rand.nextInt(10);
            numbers.add(number);
        }

        return numbers;
    }

    /**
     * Get the sum of two {@link Number}.
     * @param n1 {@link Number} instance.
     * @param n2 {@link Number} instance.
     * @return {@link Double} value.
     */
    public static double sum(@NotNull Number n1, @NotNull Number n2) {
        return n1.doubleValue() + n2.doubleValue();
    }

    /**
     * Get the sum of all {@link Number}.
     * @param numbers {@link Iterable} of {@link Number}.
     * @return {@link Double} value.
     */
    public static double sum(@NotNull Iterable<? extends Number> numbers) {
        double sum = 0;

        for (Number n : numbers) {
            sum = HPNumbers.sum(sum, n);
        }

        return sum;
    }

    /**
     * Increment some {@link Integer} by 1.
     * @param number {@link Integer} value.
     * @return {@link Integer} value.
     */
    public static int incrementByOne(int number) {
        return number + 1;
    }

    private HPNumbers() {}
}
