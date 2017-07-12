package org.swiften.javautilities.functional.optionf;

/**
 * Created by haipham on 7/12/17.
 */

import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.functional.tryf.TryConvertibleType;

/**
 * Option monad interface.
 * @param <Val> Generics parameter.
 */
public interface OptionType<Val> extends OptionConvertibleType<Val>, TryConvertibleType<Val> {
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
     * Get the associated {@link Val} instance.
     * @return {@link Val} instance.
     */
    @Nullable Val get();

    /**
     * Get the associated {@link Val} instance, or throw {@link Exception} if it
     * is not available.
     * @param e {@link Exception} instance.
     * @throws Exception If not available.
     * @return {@link Val} instance.
     */
    @NotNull Val getOrThrow(@NotNull Exception e) throws Exception;

    /**
     * Get the associated {@link Val} instance, or return a fallback.
     * @param value {@link Val} instance.
     * @return {@link Val} instance.
     */
    @NotNull Val getOrElse(@NotNull Val value);

    /**
     * Functor.
     * @param transform Transform {@link Function} from {@link Val} to {@link Val1}.
     * @param <Val1> Generics parameter.
     * @return {@link Option<Val1>} instance.
     */
    @NotNull
    <Val1> Option<Val1> map(@NotNull Function<? super Val, ? extends Val1> transform);

    /**
     * Applicative.
     * @param transform {@link OptionConvertibleType} of transform {@link Function}.
     * @param <Val1> Generics parameter.
     * @return {@link Option<Val1>}
     */
    @NotNull
    <Val1> Option<Val1> apply(@NotNull OptionConvertibleType<Function<? super Val, ? extends Val1>> transform);

    /**
     * Monad.
     * @param transform Transform {@link Function} from {@link Val} to {@link Option<Val1>}.
     * @param <Val1> Generics parameter.
     * @return {@link Option<Val1>} instance.
     */
    @NotNull
    <Val1> Option<Val1> flatMap(@NotNull Function<? super Val, ? extends OptionConvertibleType<Val1>> transform);
}
