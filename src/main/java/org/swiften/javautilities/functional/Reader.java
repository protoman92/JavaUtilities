package org.swiften.javautilities.functional;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.object.HPObjects;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by haipham on 7/13/17.
 */
public final class Reader<Env, Val> implements ReaderType<Env, Val> {
    /**
     * Create {@link Reader} that simply return the same {@link Env}.
     * @param <Env> Generics parameter.
     * @return {@link Reader} instance.
     */
    @NotNull
    public static <Env> Reader<Env, Env> eq() {
        return from(HPObjects.<Env>eqFn());
    }

    /**
     * Get {@link Reader} that simply returns some {@link Val} instance.
     * @param VAL {@link Val} instance.
     * @param <Env> Generics parameter.
     * @param <Val> Generics parameter.
     * @return {@link Reader} instance.
     */
    @NotNull
    public static <Env, Val> Reader<Env, Val> just(@NotNull final Val VAL) {
        return from(new Function<Env, Val>() {
            @NotNull
            @Override
            public Val apply(@NotNull Env env) throws Exception {
                return VAL;
            }
        });
    }

    /**
     * Get {@link Reader}.
     * @param f {@link Function} instance.
     * @param <Env> Generics parameter.
     * @param <Val> Generics parameter.
     * @return {@link Reader} instance.
     */
    @NotNull
    public static <Env, Val> Reader<Env, Val> from(@NotNull Function<? super Env, ? extends Val> f) {
        return new Reader<Env, Val>(f);
    }

    /**
     * Zip two {@link ReaderConvertibleType} using {@link BiFunction}.
     * @param r1 {@link ReaderConvertibleType} instance.
     * @param r2 {@link ReaderConvertibleType} instance.
     * @param f {@link BiFunction} instance.
     * @param <Env> Generics parameter.
     * @param <Val> Generics parameter.
     * @param <Val1> Generics parameter.
     * @param <Val2> Generics parameter.
     * @return {@link Reader} instance.
     */
    @NotNull
    public static <Env, Val, Val1, Val2> Reader<Env, Val2> zip(
        @NotNull ReaderConvertibleType<Env, Val> r1,
        @NotNull ReaderConvertibleType<Env, Val1> r2,
        @NotNull BiFunction<Val, Val1, Val2> f
    ) {
        return r1.asReader().zipWith(r2, f);
    }

    /**
     * Zip {@link Iterable} of {@link ReaderConvertibleType}.
     * @param READERS {@link Iterable} of {@link ReaderConvertibleType}.
     * @param TRANSFORM {@link Function} instance.
     * @param <Env> Generics parameter.
     * @param <Val> Generics parameter.
     * @param <Val1> Generics parameter.
     * @return {@link Reader} instance.
     */
    @NotNull
    public static <Env, Val, Val1> Reader<Env, Val1> zip(
        @NotNull final Iterable<? extends ReaderConvertibleType<Env, Val>> READERS,
        @NotNull final Function<? super Iterable<Val>, ? extends Val1> TRANSFORM
    ) {
        return from(new Function<Env, Val1>() {
            @NotNull
            @Override
            public Val1 apply(@NotNull Env env) throws Exception {
                List<Val> values = new LinkedList<Val>();

                for (ReaderConvertibleType<Env, Val> reader: READERS) {
                    values.add(reader.asReader().run(env));
                }

                return TRANSFORM.apply(values);
            }
        });
    }

    @NotNull private final Function<? super Env, ? extends Val> F;

    private Reader(@NotNull Function<? super Env, ? extends Val> f) {
        F = f;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Reader} instance.
     */
    @NotNull
    @Override
    public Reader<Env, Val> asReader() {
        return this;
    }

    /**
     * Override this method to provide default implementation.
     * @param env {@link Env} instance.
     * @return {@link Val} instance.
     * @throws Exception If the computation throws.
     */
    @NotNull
    @Override
    public Val run(@NotNull Env env) throws Exception {
        return F.apply(env);
    }

    /**
     * Override this method to provide default implementation.
     * @param ENV Generics parameter.
     * @return {@link Try} instance.
     */
    @NotNull
    @Override
    public Try<Val> tryRun(@NotNull final Env ENV) {
        final Reader<Env, Val> THIS = this;

        return Try.from(new Supplier<Val>() {
            @NotNull
            @Override
            public Val supply() throws Exception {
                return THIS.run(ENV);
            }
        });
    }

    /**
     * Override this method to provide default implementation.
     * @param TRANSFORM {@link Function} instance.
     * @param <Env1> Generics parameter.
     * @return {@link Reader} instance.
     */
    @NotNull
    @Override
    public <Env1> Reader<Env1, Val> modify(@NotNull final Function<? super Env1, ? extends Env> TRANSFORM) {
        final Reader<Env, Val> THIS = this;

        return Reader.from(new Function<Env1, Val>() {
            @NotNull
            @Override
            public Val apply(@NotNull Env1 env1) throws Exception {
                return THIS.run(TRANSFORM.apply(env1));
            }
        });
    }

    /**
     * Override this method to provide default implementation.
     * @param TRANSFORM {@link Function} instance.
     * @param <Val1> generics parameter.
     * @return {@link Reader} instance.
     */
    @NotNull
    @Override
    public <Val1> Reader<Env, Val1> map(@NotNull final Function<? super Val, ? extends Val1> TRANSFORM) {
        final Reader<Env, Val> THIS = this;

        return Reader.from(new Function<Env, Val1>() {
            @NotNull
            @Override
            public Val1 apply(@NotNull Env env) throws Exception {
                return TRANSFORM.apply(THIS.run(env));
            }
        });
    }

    /**
     * Override this method to provide default implementation.
     * @param TRANSFORM {@link Reader} instance.
     * @param <Val1> Generics parameter.
     * @return {@link Reader} instance.
     */
    @NotNull
    @Override
    public <Val1> Reader<Env, Val1> apply(@NotNull final ReaderConvertibleType<Env, Function<? super Val, ? extends Val1>> TRANSFORM) {
        final Reader<Env, Val> THIS = this;

        return TRANSFORM.asReader().flatMap(new Function<Function<? super Val, ? extends Val1>, ReaderConvertibleType<Env, Val1>>() {
            @NotNull
            @Override
            public ReaderConvertibleType<Env, Val1> apply(@NotNull final Function<? super Val, ? extends Val1> FUNCTION) throws Exception {
                return THIS.map(new Function<Val, Val1>() {
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
     * @param TRANSFORM {@link Function} instance.
     * @param <Val1> Generics parameter.
     * @return {@link Reader instance}.
     */
    @NotNull
    @Override
    public <Val1> Reader<Env, Val1> flatMap(@NotNull final Function<? super Val, ? extends ReaderConvertibleType<Env, Val1>> TRANSFORM) {
        final Reader<Env, Val> THIS = this;

        return Reader.from(new Function<Env, Val1>() {
            @NotNull
            @Override
            public Val1 apply(@NotNull Env env) throws Exception {
                return TRANSFORM.apply(THIS.run(env)).asReader().run(env);
            }
        });
    }

    /**
     * Override this method to provide default implementation.
     * @param R {@link ReaderConvertibleType} instance.
     * @param F {@link BiFunction} instance.
     * @param <Val1> Generics parameter.
     * @param <Val2> Generics parameter.
     * @return {@link Reader} instance.
     */
    @NotNull
    @Override
    public <Val1, Val2> Reader<Env, Val2> zipWith(@NotNull final ReaderConvertibleType<Env, Val1> R,
                                                  @NotNull final BiFunction<Val, Val1, Val2> F) {
        return flatMap(new Function<Val, ReaderConvertibleType<Env, Val2>>() {
            @NotNull
            @Override
            public ReaderConvertibleType<Env, Val2> apply(@NotNull final Val VAL) throws Exception {
                return R.asReader().map(new Function<Val1, Val2>() {
                    @NotNull
                    @Override
                    public Val2 apply(@NotNull Val1 val1) throws Exception {
                        return F.apply(VAL, val1);
                    }
                });
            }
        });
    }
}
