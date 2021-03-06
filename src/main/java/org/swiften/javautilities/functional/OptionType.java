package org.swiften.javautilities.functional;

/**
 * Created by haipham on 7/12/17.
 */

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Option monad interface.
 * @param <Val> Generics parameter.
 */
public interface OptionType<Val> extends OptionConvertibleType<Val>, TryConvertibleType<Val>, MaybeType<Val> {
    /**
     * Check if there is some {@link Val} value available.
     * @return {@link Boolean} value.
     */
    boolean isPresent();

    /**
     * Check if there is nothing available.
     * @return {@link Boolean} value.
     */
    boolean isNothing();

    /**
     * Functor.
     * @param transform Transform {@link Function} from {@link Val} to {@link Val1}.
     * @param <Val1> Generics parameter.
     * @return {@link Option} instance.
     */
    @NotNull <Val1> Option<Val1> map(@NotNull Function<? super Val, ? extends Val1> transform);

    /**
     * Applicative.
     * @param transform {@link OptionConvertibleType} of transform {@link Function}.
     * @param <Val1> Generics parameter.
     * @return {@link Option}
     */
    @NotNull <Val1> Option<Val1> apply(@NotNull OptionConvertibleType<Function<? super Val, ? extends Val1>> transform);

    /**
     * Monad.
     * @param transform Transform {@link Function} from {@link Val} to {@link Option}.
     * @param <Val1> Generics parameter.
     * @return {@link Option} instance.
     */
    @NotNull <Val1> Option<Val1> flatMap(@NotNull Function<? super Val, ? extends OptionConvertibleType<Val1>> transform);

    /**
     * Zip with another {@link OptionConvertibleType} and a {@link BiFunction}
     * to create a {@link Option}.
     * @param option2 {@link OptionConvertibleType} instance.
     * @param transform Transform {@link BiFunction} from {@link Val} and
     * {@link Val2} to {@link Val3}.
     * @param <Val2> Generics parameter.
     * @param <Val3> Generics parameter.
     * @return {@link Option} instance.
     */
    @NotNull <Val2,Val3> Option<Val3> zipWith(
        @NotNull OptionConvertibleType<Val2> option2,
        @NotNull BiFunction<? super Val,? super Val2,? extends Val3> transform);
}
