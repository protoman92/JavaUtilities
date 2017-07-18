package org.swiften.javautilities.functional;

import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by haipham on 7/12/17.
 */
public abstract class Option<Val> implements OptionType<Val> {
    @NotNull static final String VALUE_UNAVAILABLE = "Value unavailable";

    /**
     * Get {@link Some}.
     * @param value {@link Val} instance.
     * @param <Val> Generics parameter.
     * @return {@link Option} instance.
     */
    @NotNull
    public static <Val> Option<Val> some(@NotNull Val value) {
        return new Some<Val>(value);
    }

    /**
     * Get {@link Nothing}.
     * @param <Val> Generics parameter.
     * @return {@link Option} instance.
     */
    @NotNull
    public static <Val> Option<Val> nothing() {
        return new Nothing<Val>();
    }

    Option() {}

    /**
     * Override this method to provide default implementation.
     * @return {@link Option} instance.
     */
    @NotNull
    @Override
    public Option<Val> asOption() {
        return this;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Boolean} value.
     */
    @Override
    public boolean isPresent() {
        return this instanceof Some;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Boolean} value.
     */
    @Override
    public boolean isNothing() {
        return this instanceof Nothing;
    }

    /**
     * Represent some {@link Val} instance.
     * @param <Val> Generics parameter.
     */
    private static final class Some<Val> extends Option<Val> {
        @NotNull private final Val VALUE;

        Some(@NotNull Val value) {
            VALUE = value;
        }

        @NotNull
        @Override
        public String toString() {
            return String.format("Present: %s", VALUE);
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Try} instance.
         */
        @NotNull
        @Override
        public Try<Val> asTry() {
            return Try.success(VALUE);
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
         * @param e {@link Exception} instance.
         * @return {@link Val} instance.
         * @throws Exception If not available.
         */
        @NotNull
        @Override
        public Val getOrThrow(@NotNull Exception e) throws Exception {
            return VALUE;
        }

        /**
         * Override this method to provide default implementation.
         * @param value {@link Val} instance.
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
         * @return {@link Option} instance.
         */
        @NotNull
        @Override
        public <Val1> Option<Val1> map(@NotNull Function<? super Val, ? extends Val1> transform) {
            try {
                return new Some<Val1>(transform.apply(VALUE));
            } catch (Exception e) {
                return new Nothing<Val1>();
            }
        }

        /**
         * Override this method to provide default implementation.
         * @param transform {@link OptionConvertibleType} of transform {@link Function}.
         * @param <Val1> Generics parameter.
         * @return {@link Option}
         */
        @NotNull
        @Override
        public <Val1> Option<Val1> apply(@NotNull OptionConvertibleType<Function<? super Val, ? extends Val1>> transform) {
            return transform.asOption().flatMap(new Function<Function<? super Val, ? extends Val1>, OptionConvertibleType<Val1>>() {
                @NotNull
                @Override
                public OptionConvertibleType<Val1> apply(@NotNull final Function<? super Val, ? extends Val1> FUNCTION) throws Exception {
                    return Some.this.map(new Function<Val, Val1>() {
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
         * @param transform Transform {@link Function} from {@link Val} to {@link Option}.
         * @param <Val1> Generics parameter.
         * @return {@link Option} instance.
         */
        @NotNull
        @Override
        public <Val1> Option<Val1> flatMap(@NotNull Function<? super Val, ? extends OptionConvertibleType<Val1>> transform) {
            try {
                return transform.apply(VALUE).asOption();
            } catch (Exception e) {
                return new Nothing<Val1>();
            }
        }
    }

    /**
     * Represent nothing.
     * @param <Val> Generics parameter.
     */
    private static final class Nothing<Val> extends Option<Val> {
        Nothing() {}

        @NotNull
        @Override
        public String toString() {
            return "Nothing";
        }

        /**
         * Override this method to provide default implementation.
         * @return {@link Try} instance.
         */
        @NotNull
        @Override
        public Try<Val> asTry() {
            return Try.failure(VALUE_UNAVAILABLE);
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
         * @param e {@link Exception} instance.
         * @return {@link Val} instance.
         * @throws Exception If not available.
         */
        @NotNull
        @Override
        public Val getOrThrow(@NotNull Exception e) throws Exception {
            throw e;
        }

        /**
         * Override this method to provide default implementation.
         * @param value {@link Val} instance.
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
         * @return {@link Option} instance.
         */
        @NotNull
        @Override
        public <Val1> Option<Val1> map(@NotNull Function<? super Val, ? extends Val1> transform) {
            return new Nothing<Val1>();
        }

        /**
         * Override this method to provide default implementation.
         * @param transform {@link OptionConvertibleType} of transform {@link Function}.
         * @param <Val1> Generics parameter.
         * @return {@link Option}
         */
        @NotNull
        @Override
        public <Val1> Option<Val1> apply(@NotNull OptionConvertibleType<Function<? super Val, ? extends Val1>> transform) {
            return new Nothing<Val1>();
        }

        /**
         * Override this method to provide default implementation.
         * @param transform Transform {@link Function} from {@link Val} to {@link Option}.
         * @param <Val1> Generics parameter.
         * @return {@link Option} instance.
         */
        @NotNull
        @Override
        public <Val1> Option<Val1> flatMap(@NotNull Function<? super Val, ? extends OptionConvertibleType<Val1>> transform) {
            return new Nothing<Val1>();
        }
    }
}
