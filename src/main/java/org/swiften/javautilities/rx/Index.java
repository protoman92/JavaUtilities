package org.swiften.javautilities.rx;

/**
 * Created by haipham on 5/6/17.
 */

import org.swiften.javautilities.functional.Tuple;
import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Convenience class to use with {@link Flowable#zip(Iterable, Function)}
 * and {@link io.reactivex.Flowable#zipWith(Iterable, BiFunction)}.
 * @param <T> Non-bound generics.
 */
public final class Index<T> {
    @NotNull public final T OBJECT;

    public final int INDEX;

    public Index(@NotNull T object, int index) {
        OBJECT = object;
        INDEX = index;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o instanceof Index) {
            Index index = (Index)o;
            return index.OBJECT.equals(OBJECT) && index.INDEX == INDEX;
        } else {
            return false;
        }
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("%s, index: %d", OBJECT, INDEX);
    }

    /**
     * Produce a
     * {@link Tuple}
     * from {@link #INDEX} and {@link #OBJECT}.
     * @return {@link Tuple}
     * instance.
     */
    @NotNull
    public Tuple<Integer,T> toZipped() {
        return Tuple.of(INDEX, OBJECT);
    }
}