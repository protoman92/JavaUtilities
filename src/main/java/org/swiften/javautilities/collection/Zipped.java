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
public final class Zipped<A,B> {
    @Nullable public final A FIRST;
    @Nullable public final B SECOND;

    public Zipped(@Nullable A a, @Nullable B b) {
        FIRST = a;
        SECOND = b;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o instanceof Zipped) {
            Zipped z = (Zipped)o;

            if (FIRST != null && SECOND != null) {
                return FIRST.equals(z.FIRST) && SECOND.equals(z.SECOND);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
