package org.swiften.javautilities.functional;

/**
 * Created by haipham on 7/13/17.
 */

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Reader monad interface.
 * @param <Env> Generics parameter.
 * @param <Val> Generics parameter.
 */
public interface ReaderType<Env,Val> extends ReaderConvertibleType<Env,Val> {
    /**
     * Run the associated computation with some {@link Env} instance.
     * @param env {@link Env} instance.
     * @return {@link Val} instance.
     * @throws Exception If the computation throws.
     */
    @NotNull Val run(@NotNull Env env) throws Exception;

    /**
     * Same as above, but wrapped in {@link Try}.
     * @param env {@link Env} instance.
     * @return {@link Try} instance.
     */
    @NotNull Try<Val> tryRun(@NotNull Env env);

    /**
     * Modify the environment.
     * @param transform {@link Function} to transform {@link Env1} to {@link Env}.
     * @param <Env1> Generics parameter.
     * @return {@link Reader} instance.
     */
    @NotNull <Env1> Reader<Env1, Val> modify(@NotNull Function<? super Env1, ? extends Env> transform);

    /**
     * Functor.
     * @param transform Transform {@link Function} from {@link Val} to {@link Val1}.
     * @param <Val1> Generics parameter.
     * @return {@link Reader} instance.
     */
    @NotNull <Val1> Reader<Env, Val1> map(@NotNull Function<? super Val, ? extends Val1> transform);

    /**
     * Applicative.
     * @param transform {@link ReaderConvertibleType} of transform {@link Function}.
     * @param <Val1> Generics parameter.
     * @return {@link Reader}.
     */
    @NotNull <Val1> Reader<Env, Val1> apply(@NotNull ReaderConvertibleType<Env, Function<? super Val, ? extends Val1>> transform);

    /**
     * Monad.
     * @param transform Transform {@link Function} from {@link Val} to {@link ReaderConvertibleType}.
     * @param <Val1> Generics parameter.
     * @return {@link ReaderConvertibleType} instance.
     */
    @NotNull <Val1> Reader<Env, Val1> flatMap(@NotNull Function<? super Val, ? extends ReaderConvertibleType<Env, Val1>> transform);

    /**
     * Zip with another {@link Reader} to create {@link Reader} using some {@link BiFunction}.
     * @param r {@link ReaderConvertibleType} instance.
     * @param f {@link BiFunction} instance.
     * @param <Val1> Generics parameter.
     * @param <Val2> Generics parameter.
     * @return {@link Reader} instance.
     */
    @NotNull
    <Val1, Val2> Reader<Env, Val2> zipWith(@NotNull ReaderConvertibleType<Env, Val1> r,
                                           @NotNull BiFunction<Val, Val1, Val2> f);
}
