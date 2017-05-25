package org.swiften.javautilities.number;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by haipham on 5/6/17.
 */
public final class NumberTestUtil {
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

    private NumberTestUtil() {}
}
