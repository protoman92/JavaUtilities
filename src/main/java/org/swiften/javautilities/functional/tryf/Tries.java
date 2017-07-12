package org.swiften.javautilities.functional.tryf;

/**
 * Created by haipham on 7/12/17.
 */

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for {@link TryType}.
 */
public final class Tries {
    /**
     * Get {@link Function} to convert {@link Val} instance to {@link Try<Val>}.
     * @param <Val> Generics parameter.
     * @return {@link Function} instance.
     * @see Try#success(Object)
     */
    @NotNull
    public static <Val> Function<? super Val, Try<Val>> successFn() {
        return new Function<Val, Try<Val>>() {
            @NotNull
            @Override
            public Try<Val> apply(@NotNull Val val) throws Exception {
                return Try.success(val);
            }
        };
    }

    /**
     * Get {@link Function} to convert from {@link Throwable} to {@link Try<Val>}.
     * @param <Val> Generics parameter.
     * @return {@link Function} instance.
     * @see Try#failure(Throwable)
     */
    @NotNull
    public static <Val> Function<? super Throwable, Try<Val>> failureFn() {
        return new Function<Throwable, Try<Val>>() {
            @NotNull
            @Override
            public Try<Val> apply(@NotNull Throwable t) throws Exception {
                return Try.failure(t);
            }
        };
    }

    /**
     * Get {@link Function} to force-get value from {@link TryConvertibleType<Val>}.
     * @param <Val> Generics parameter.
     * @return {@link Function} instance.
     * @see TryType#getOrThrow()
     */
    @NotNull
    public static <Val> Function<? super TryConvertibleType<Val>, ? extends Val> getFn() {
        return new Function<TryConvertibleType<Val>, Val>() {
            @NotNull
            @Override
            public Val apply(@NotNull TryConvertibleType<Val> tryInstance) throws Exception {
                return tryInstance.asTry().getOrThrow();
            }
        };
    }

    private Tries() {}
}
