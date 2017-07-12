package org.swiften.javautilities.functional.tryf;

import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.functional.optionf.Option;
import org.swiften.javautilities.functional.optionf.OptionType;

/**
 * Created by haipham on 11/7/17.
 */
public abstract class Try<Val> implements TryType<Val> {
    /**
     * Get {@link Success<Val>}.
     * @param value {@link Val} instance.
     * @param <Val> Generics parameter.
     * @return {@link Try<Val>} instance.
     */
    @NotNull
    public static <Val> Try<Val> success(@NotNull Val value) {
        return new Success<Val>(value);
    }

    /**
     * Get {@link Failure<Val>}.
     * @param e {@link Exception} instance.
     * @param <Val> Generics parameter.
     * @return {@link Try<Val>} instance.
     */
    @NotNull
    public static <Val> Try<Val> failure(@NotNull Exception e) {
        return new Failure<Val>(e);
    }

    /**
     * Get {@link Failure<Val>}.
     * @param t {@link Throwable} instance.
     * @param <Val> Generics parameter.
     * @return {@link Try<Val>} instance.
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
     * Get {@link Failure<Val>}.
     * @param error {@link String} value.
     * @param <Val> Generics parameter.
     * @return {@link Try<Val>} instance.
     * @see #failure(Exception)
     */
    @NotNull
    public static <Val> Try<Val> failure(@NotNull String error) {
        return failure(new Exception(error));
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Try<Val>} instance.
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
     * Represent success {@link Try<Val>}.
     * @param <Val> Generics parameter.
     */
    private static final class Success<Val> extends Try<Val> {
        @NotNull private final Val VALUE;

        Success(@NotNull Val value) {
            VALUE = value;
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Option<Val>} instance.
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
         * @param transform Transform {@link Function} from {@link Val} to {@link Val1}.
         * @param <Val1> Generics parameter.
         * @return {@link Try<Val1>} instance.
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
         * @return {@link Try<Val1>} instance.
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
         * @param transform Transform {@link Function} from {@link Val} to {@link Try<Val1>}.
         * @param <Val1> Generics parameter.
         * @return {@link Try<Val1>} instance.
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
     * Represent failure {@link Try<Val>}.
     * @param <Val> Generics parameter.
     */
    private static final class Failure<Val> extends Try<Val> {
        @NotNull private final Exception ERROR;

        Failure(@NotNull Exception e) {
            ERROR = e;
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Option<Val>} instance.
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
         * @param transform Transform {@link Function} from {@link Val} to {@link Val1}.
         * @param <Val1> Generics parameter.
         * @return {@link Try<Val1>} instance.
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
         * @return {@link Try<Val1>} instance.
         */
        @NotNull
        @Override
        public <Val1> Try<Val1> apply(@NotNull TryConvertibleType<Function<? super Val, ? extends Val1>> transform) {
            return new Failure<Val1>(ERROR);
        }

        /**
         * Override this method to provide default implementation.
         * @param transform Transform {@link Function} from {@link Val} to {@link Try<Val1>}.
         * @param <Val1> Generics parameter.
         * @return {@link Try<Val1>} instance.
         */
        @NotNull
        @Override
        public <Val1> Try<Val1> flatMap(@NotNull Function<? super Val, ? extends TryConvertibleType<Val1>> transform) {
            return new Failure<Val1>(ERROR);
        }
    }
}
