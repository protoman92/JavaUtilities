package org.swiften.javautilities.functional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by haipham on 5/6/17.
 */

/**
 * Represent a tuple of some data.
 * @param <A> Generics parameter.
 * @param <B> Generics parameter.
 */
public final class Tuple<A, B> {
    /**
     * Create a new {@link Tuple} instance.
     * @param a {@link A} instance.
     * @param b {@link B} instance.
     * @param <A> Generics parameter.
     * @param <B> Generics parameter.
     * @return {@link Tuple} instance.
     */
    @NotNull
    public static <A, B> Tuple<A, B> of(@NotNull A a, @NotNull B b) {
        return new Tuple<A, B>(a, b);
    }

    @NotNull public final A A;
    @NotNull public final B B;

    private Tuple(@NotNull A a, @NotNull B b) {
        A = a;
        B = b;
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("First: %s, Second: %s", A, B);
    }

    /**
     * Get {@link #A}.
     * @return {@link A} instance.
     */
    @NotNull
    public A first() {
        return A;
    }

    /**
     * Get {@link #B}.
     * @return {@link B} instance.
     */
    @NotNull
    public B second() {
        return B;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o instanceof Tuple) {
            Tuple z = (Tuple)o;
            return A.equals(z.A) && B.equals(z.B);
        } else {
            return false;
        }
    }
}
