package org.swiften.javautilities.functional;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by haipham on 11/7/17.
 */

/**
 * Try monad interface.
 * @param <Val> Generics parameter.
 */
public interface TryType<Val> extends TryConvertibleType<Val>, OptionConvertibleType<Val>, MaybeType<Val> {
    /**
     * Check whether there is a success value.
     * @return {@link Boolean} value.
     */
    boolean isSuccess();

    /**
     * Check whether there is a failure error.
     * @return {@link Boolean} value.
     */
    boolean isFailure();

    /**
     * Get the failure {@link Exception}.
     * @return {@link Exception} instance.
     */
    @Nullable Exception getError();

    /**
     * Get the success {@link Val}, or throw the failure {@link Exception}.
     * @return {@link Val} instance.
     * @throws Exception Failure {@link Exception}.
     */
    @NotNull Val getOrThrow() throws Exception;

    /**
     * Functor.
     * @param transform Transform {@link Function} from {@link Val} to {@link Val1}.
     * @param <Val1> Generics parameter.
     * @return {@link Try} instance.
     */
    @NotNull <Val1> Try<Val1> map(@NotNull Function<? super Val,? extends Val1> transform);

    /**
     * Applicative.
     * @param transform {@link TryConvertibleType} of transform {@link Function}.
     * @param <Val1> Generics parameter.
     * @return {@link Try}
     */
    @NotNull <Val1> Try<Val1> apply(@NotNull TryConvertibleType<Function<? super Val,? extends Val1>> transform);

    /**
     * Monad.
     * @param transform Transform {@link Function} from {@link Val} to {@link Try}.
     * @param <Val1> Generics parameter.
     * @return {@link Try} instance.
     */
    @NotNull <Val1> Try<Val1> flatMap(@NotNull Function<? super Val,? extends TryConvertibleType<Val1>> transform);

    /**
     * Zip with another {@link TryConvertibleType} and a {@link BiFunction}
     * to create a {@link Try}.
     * @param try2 {@link TryConvertibleType} instance.
     * @param transform Transform {@link BiFunction} from {@link Val} and
     * {@link Val2} to {@link Val3}.
     * @param <Val2> Generics parameter.
     * @param <Val3> Generics parameter.
     * @return {@link Try} instance.
     */
    @NotNull <Val2,Val3> Try<Val3> zipWith(
        @NotNull TryConvertibleType<Val2> try2,
        @NotNull BiFunction<? super Val,? super Val2,? extends Val3> transform);
}
