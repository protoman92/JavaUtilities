package org.swiften.javautilities.rx;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import org.swiften.javautilities.object.ObjectUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Created by haipham on 3/31/17.
 */
public final class RxUtil {
    /**
     * Apply {@link FlowableTransformer} to an existing {@link Flowable}.
     * Applicable to {@link Flowable#compose(FlowableTransformer)}.
     * @param <T> Generics parameter.
     * @return {@link FlowableTransformer} instance.
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
     * Create {@link Flowable} from {@link Collection} of {@link T}. This
     * {@link Flowable} emits {@link Index}, allowing us to access the original
     * {@link T} object, as well as its index in the {@link Collection}.
     * @param collection The {@link Collection} from which {@link Flowable}
     *                   will be constructed.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
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
     * Create {@link Flowable} from a varargs of {@link T}. This
     * {@link Flowable} emits {@link Index}, allowing use to access the
     * original {@link T} object, as well as its index in the varargs.
     * @param objects A varargs of {@link T}.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #from(Collection)
     */
    @NotNull
    public static <T> Flowable<Index<T>> from(@NotNull T...objects) {
        return from(Arrays.asList(objects));
    }

    /**
     * Produce an error {@link Flowable} with an error message.
     * @param error {@link String} value.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see Flowable#error(Throwable)
     * @see #error()
     */
    @NotNull
    public static <T> Flowable<T> error(@Nullable String error) {
        if (ObjectUtil.nonNull(error)) {
            return Flowable.error(new RuntimeException(error));
        } else {
            return error();
        }
    }

    /**
     * Produce an error with a formatted {@link String} message.
     * @param format {@link String} value.
     * @param args A varargs of {@link Object}.
     * @param <T> Generics paramter.
     * @return {@link Flowable} instance.
     * @see #error(String)
     */
    @NotNull
    public static <T> Flowable<T> errorF(@NotNull String format,
                                         @NotNull Object...args) {
        return error(String.format(format, args));
    }

    /**
     * Produce an error {@link Flowable} with a blank message.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #error(String)
     */
    @NotNull
    public static <T> Flowable<T> error() {
        return error("");
    }

    private RxUtil() {}
}
