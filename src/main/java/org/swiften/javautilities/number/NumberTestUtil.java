package org.swiften.javautilities.number;

import java.util.Random;

/**
 * Created by haipham on 5/6/17.
 */
public final class NumberTestUtil {
    /**
     * Produce a random number between two bounds.
     * @param from The inclusive lower bound.
     * @param to The non-inclusive upper bound.
     * @return An {@link Integer} value.
     */
    public static int randomBetween(int from, int to) {
        return new Random().nextInt(to - from) + from;
    }

    private NumberTestUtil() {}
}
