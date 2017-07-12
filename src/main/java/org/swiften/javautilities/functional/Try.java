package org.swiften.javautilities.functional;

import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by haipham on 11/7/17.
 */
public abstract class Try<Val> implements TryType<Val> {
    /**
     * Get {@link Success}.
     * @param value {@link Val} instance.
     * @param <Val> Generics parameter.
     * @return {@link Try} instance.
     */
    @NotNull
    public static <Val> Try<Val> success(@NotNull Val value) {
        return new Success<Val>(value);
    }

    /**
     * Get {@link Failure}.
     * @param e {@link Exception} instance.
     * @param <Val> Generics parameter.
     * @return {@link Try} instance.
     */
    @NotNull
    public static <Val> Try<Val> failure(@NotNull Exception e) {
        return new Failure<Val>(e);
    }

    /**
     * Get {@link Failure}.
     * @param t {@link Throwable} instance.
     * @param <Val> Generics parameter.
     * @return {@link Try} instance.
     * @see #failure(Exception)
     */
    @NotNull
    public static <Val> Try<Val> failure(@NotNull Throwable t) {
        if (t instanceof Exception) {
            return failure((Exception)t);
        } else {
            return failure(new Exception(t));
        }
    }

    /**
     * Get {@link Failure}.
     * @param error {@link String} value.
     * @param <Val> Generics parameter.
     * @return {@link Try} instance.
     * @see #failure(Exception)
     */
    @NotNull
    public static <Val> Try<Val> failure(@NotNull String error) {
        return failure(new Exception(error));
    }

    /**
     * Get {@link Try} based on some {@link Supplier}.
     * @param supplier {@link Supplier} instance.
     * @param <Val> Generics parameter.
     * @return {@link Try} instance.
     */
    @NotNull
    public static <Val> Try<Val> from(@NotNull Supplier<Val> supplier) {
        try {
            return success(supplier.supply());
        } catch (Exception e) {
            return failure(e);
        }
    }

    Try() {}

    /**
     * Override this method to provide default implementation.
     * @return {@link Try} instance.
     * @see TryConvertibleType#asTry()
     */
    @NotNull
    @Override
    public Try<Val> asTry() {
        return this;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Boolean} value.
     */
    @Override
    public boolean isSuccess() {
        return this instanceof Success;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Boolean} value.
     */
    @Override
    public boolean isFailure() {
        return this instanceof Failure;
    }

    /**
     * Represent success {@link Try}.
     * @param <Val> Generics parameter.
     */
    private static final class Success<Val> extends Try<Val> {
        @NotNull private final Val VALUE;

        Success(@NotNull Val value) {
            VALUE = value;
        }

        @NotNull
        @Override
        public String toString() {
            return String.format("Success: %s", VALUE);
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Option} instance.
         */
        @NotNull
        @Override
        public Option<Val> asOption() {
            return Option.some(VALUE);
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Val} instance.
         * @throws Exception If failure.
         */
        @NotNull
        @Override
        public Val getOrThrow() throws Exception {
            return VALUE;
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Val} instance.
         */
        @Nullable
        @Override
        public Val get() {
            return VALUE;
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Exception} instance.
         */
        @Nullable
        @Override
        public Exception getError() {
            return null;
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Val} instance.
         * @throws Exception If failure.
         */
        @NotNull
        @Override
        public Val getOrThrow(@NotNull Exception e) throws Exception {
            return VALUE;
        }


        /**
         * Override this method to provide default implementation.
         * @return {@link Val} instance.
         */
        @NotNull
        @Override
        public Val getOrElse(@NotNull Val value) {
            return VALUE;
        }

        /**
         * Override this method to provide default implementation.
         * @param transform Transform {@link Function} from {@link Val} to {@link Val1}.
         * @param <Val1> Generics parameter.
         * @return {@link Try} instance.
         */
        @NotNull
        @Override
        public <Val1> Try<Val1> map(@NotNull Function<? super Val, ? extends Val1> transform) {
            try {
                return new Success<Val1>(transform.apply(VALUE));
            } catch (Exception e) {
                return new Failure<Val1>(e);
            }
        }

        /**
         * Override this method to provide default implementation.
         * @param transform {@link TryConvertibleType} of transform {@link Function}.
         * @param <Val1> Generics parameter.
         * @return {@link Try} instance.
         */
        @NotNull
        @Override
        public <Val1> Try<Val1> apply(@NotNull TryConvertibleType<Function<? super Val, ? extends Val1>> transform) {
            return transform.asTry().flatMap(new Function<Function<? super Val, ? extends Val1>, TryConvertibleType<Val1>>() {
                @NotNull
                @Override
                public TryConvertibleType<Val1> apply(@NotNull final Function<? super Val, ? extends Val1> FUNCTION) throws Exception {
                    return Success.this.map(new Function<Val, Val1>() {
                        @NotNull
                        @Override
                        public Val1 apply(@NotNull Val val) throws Exception {
                            return FUNCTION.apply(val);
                        }
                    });
                }
            });
        }

        /**
         * Override this method to provide default implementation.
         * @param transform Transform {@link Function} from {@link Val} to {@link Try}.
         * @param <Val1> Generics parameter.
         * @return {@link Try} instance.
         */
        @NotNull
        @Override
        public <Val1> Try<Val1> flatMap(@NotNull Function<? super Val, ? extends TryConvertibleType<Val1>> transform) {
            try {
                return transform.apply(getOrThrow()).asTry();
            } catch (Exception e) {
                return new Failure<Val1>(e);
            }
        }
    }

    /**
     * Represent failure {@link Try}.
     * @param <Val> Generics parameter.
     */
    private static final class Failure<Val> extends Try<Val> {
        @NotNull private final Exception ERROR;

        Failure(@NotNull Exception e) {
            ERROR = e;
        }

        public String toString() {
            return String.format("Failure: %s", ERROR);
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Option} instance.
         */
        @NotNull
        @Override
        public Option<Val> asOption() {
            return Option.nothing();
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Val} instance.
         * @throws Exception If failure.
         */
        @NotNull
        @Override
        public Val getOrThrow() throws Exception {
            throw ERROR;
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Val} instance.
         */
        @Nullable
        @Override
        public Val get() {
            return null;
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Exception} instance.
         */
        @Nullable
        @Override
        public Exception getError() {
            return ERROR;
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Val} instance.
         * @throws Exception If failure.
         */
        @NotNull
        @Override
        public Val getOrThrow(@NotNull Exception e) throws Exception {
            throw e;
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Val} instance.
         */
        @NotNull
        @Override
        public Val getOrElse(@NotNull Val value) {
            return value;
        }

        /**
         * Override this method to provide default implementation.
         * @param transform Transform {@link Function} from {@link Val} to {@link Val1}.
         * @param <Val1> Generics parameter.
         * @return {@link Try} instance.
         */
        @NotNull
        @Override
        public <Val1> Try<Val1> map(@NotNull Function<? super Val, ? extends Val1> transform) {
            return new Failure<Val1>(ERROR);
        }

        /**
         * Override this method to provide default implementation.
         * @param transform {@link TryConvertibleType} of transform {@link Function}.
         * @param <Val1> Generics parameter.
         * @return {@link Try} instance.
         */
        @NotNull
        @Override
        public <Val1> Try<Val1> apply(@NotNull TryConvertibleType<Function<? super Val, ? extends Val1>> transform) {
            return new Failure<Val1>(ERROR);
        }

        /**
         * Override this method to provide default implementation.
         * @param transform Transform {@link Function} from {@link Val} to {@link Try}.
         * @param <Val1> Generics parameter.
         * @return {@link Try} instance.
         */
        @NotNull
        @Override
        public <Val1> Try<Val1> flatMap(@NotNull Function<? super Val, ? extends TryConvertibleType<Val1>> transform) {
            return new Failure<Val1>(ERROR);
        }
    }
}
