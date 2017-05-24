package org.swiften.javautilities.collection;

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
    @Nullable public final A A;
    @Nullable public final B B;

    public Zip(@Nullable A a, @Nullable B b) {
        A = a;
        B = b;
    }

    @Override
    public String toString() {
        return String.format("First: %s, Second: %s", A, B);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o instanceof Zip) {
            Zip z = (Zip)o;

            if (A != null && B != null) {
                return A.equals(z.A) && B.equals(z.B);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
