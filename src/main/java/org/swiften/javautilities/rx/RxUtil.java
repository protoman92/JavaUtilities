package org.swiften.javautilities.rx;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by haipham on 3/31/17.
 */
public final class RxUtil {
    /**
     * Apply a {@link FlowableTransformer} to an existing {@link Flowable}.
     * Applicable to {@link Flowable#compose(FlowableTransformer)}.
     * @param <T> Generics parameter.
     * @return A {@link FlowableTransformer} instance.
     * @see Flowable#compose(FlowableTransformer)
     */
    @NotNull
    public static <T> FlowableTransformer<T,T> withCommonSchedulers() {
        return new FlowableTransformer<T, T>() {
            @NonNull
            public Publisher<T> apply(@NonNull Flowable<T> upstream) {
                return upstream
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.trampoline());
            }
        };
    }

    /**
     * Create a {@link Flowable} from a {@link Collection} of {@link T}. This
     * {@link Flowable} emits {@link Index}, allowing us to access the original
     * {@link T} object, as well as its index in the {@link Collection}.
     * @param collection The {@link Collection} from which {@link Flowable}
     *                   will be constructed.
     * @param <T> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see Flowable#fromIterable(Iterable)
     * @see Flowable#range(int, int)
     * @see Flowable#zip(Publisher, Publisher, BiFunction)
     */
    @NotNull
    public static <T> Flowable<Index<T>> from(@NotNull Collection<T> collection) {
        return Flowable.zip(
            Flowable.fromIterable(collection),
            Flowable.range(0, collection.size()),

            new BiFunction<T, Integer, Index<T>>() {
                @NonNull
                public Index<T> apply(@NonNull T t, @NonNull Integer integer)
                    throws Exception
                {
                    return new Index<T>(t, integer);
                }
            }
        );
    }

    /**
     * Create a {@link Flowable} from a varargs of {@link T}. This
     * {@link Flowable} emits {@link Index}, allowing use to access the
     * original {@link T} object, as well as its index in the varargs.
     * @param objects A varargs of {@link T}.
     * @param <T> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #from(Collection)
     */
    @NotNull
    public static <T> Flowable<Index<T>> from(@NotNull T...objects) {
        return from(Arrays.asList(objects));
    }
}