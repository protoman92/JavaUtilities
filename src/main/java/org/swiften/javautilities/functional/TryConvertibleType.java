package org.swiften.javautilities.functional;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 11/7/17.
 */

/**
 * Convertible to {@link TryType}.
 * @param <Val> Generics parameter.
 */
public interface TryConvertibleType<Val> {
    /**
     * Convert the current {@link TryConvertibleType} into {@link Try}.
     * @return {@link Try} instance.
     */
    @NotNull Try<Val> asTry();
}
