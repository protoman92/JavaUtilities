package org.swiften.javautilities.functional;

/**
 * Created by haipham on 7/12/17.
 */

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;

/**
 * Utility class for {@link TryType}.
 */
public final class Tries {
    /**
     * Get {@link Function} to convert {@link Val} instance to {@link Try}.
     * @param <Val> Generics parameter.
     * @return {@link Function} instance.
     * @see Try#success(Object)
     */
    @NotNull
    public static <Val> Function<Val, Try<Val>> successFn() {
        return new Function<Val, Try<Val>>() {
            @NotNull
            @Override
            public Try<Val> apply(@NotNull Val val) throws Exception {
                return Try.success(val);
            }
        };
    }

    /**
     * Get {@link Function} to convert from {@link Throwable} to {@link Try}.
     * @param <Val> Generics parameter.
     * @return {@link Function} instance.
     * @see Try#failure(Throwable)
     */
    @NotNull
    public static <Val> Function<Throwable, Try<Val>> failureFn() {
        return new Function<Throwable, Try<Val>>() {
            @NotNull
            @Override
            public Try<Val> apply(@NotNull Throwable t) throws Exception {
                return Try.failure(t);
            }
        };
    }

    /**
     * Get {@link Function} to force-get value from {@link TryConvertibleType}.
     * @param <Val> Generics parameter.
     * @return {@link Function} instance.
     * @see TryType#getOrThrow()
     */
    @NotNull
    public static <Val> Function<TryConvertibleType<Val>, Val> getFn() {
        return new Function<TryConvertibleType<Val>, Val>() {
            @NotNull
            @Override
            public Val apply(@NotNull TryConvertibleType<Val> tryInstance) throws Exception {
                return tryInstance.asTry().getOrThrow();
            }
        };
    }

    /**
     * {@link FlowableTransformer} to wrap an emission in {@link Try}.
     * @param <T> Generics parameter.
     * @return {@link FlowableTransformer} instance.
     */
    @NotNull
    public static <T> FlowableTransformer<T, Try<T>> wrapFn() {
        return new FlowableTransformer<T, Try<T>>() {
            @NotNull
            @Override
            public Publisher<Try<T>> apply(@NotNull Flowable<T> upstream) {
                return upstream
                    .map(Tries.<T>successFn())
                    .onErrorReturn(Tries.<T>failureFn());
            }
        };
    }

    private Tries() {}
}
