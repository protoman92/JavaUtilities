package org.swiften.javautilities.collection;

import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Created by haipham on 5/6/17.
 */

/**
 * Use this class to zip two {@link Collection}.
 * @param <A> Generics parameter.
 * @param <B> Generics parameter.
 */
public final class Zip<A,B> {
    /**
     * Create a new {@link Zip} instance.
     * @param a {@link A} instance.
     * @param b {@link B} instance.
     * @param <A> Generics parameter.
     * @param <B> Generics parameter.
     * @return {@link Zip} instance.
     */
    @NotNull
    public static <A,B> Zip<A,B> of(@NonNull A a, @NotNull B b) {
        return new Zip<A,B>(a, b);
    }

    @NonNull public final A A;
    @NotNull public final B B;

    private Zip(@NonNull A a, @NonNull B b) {
        A = a;
        B = b;
    }

    @Override
    public String toString() {
        return String.format("First: %s, Second: %s", A, B);
    }

    /**
     * Get {@link A}.
     * @return {@link A} instance.
     * @see #A
     */
    @NotNull
    public A first() {
        return A;
    }

    /**
     * Get {@link B}.
     * @return {@link B} instance.
     * @see #B
     */
    @NotNull
    public B second() {
        return B;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o instanceof Zip) {
            Zip z = (Zip)o;
            return A.equals(z.A) && B.equals(z.B);
        } else {
            return false;
        }
    }
}
