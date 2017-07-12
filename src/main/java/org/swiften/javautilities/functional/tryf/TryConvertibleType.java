package org.swiften.javautilities.functional.tryf;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.functional.optionf.OptionConvertibleType;

/**
 * Created by haipham on 11/7/17.
 */

/**
 * Convertible to {@link TryType}.
 * @param <Val> Generics parameter.
 */
public interface TryConvertibleType<Val> {
    /**
     * Convert the current {@link TryConvertibleType<Val>} into {@link Try<Val>}.
     * @return {@link Try} instance.
     */
    @NotNull Try<Val> asTry();
}
