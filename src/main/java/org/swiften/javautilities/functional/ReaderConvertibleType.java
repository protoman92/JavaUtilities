package org.swiften.javautilities.functional;

/**
 * Created by haipham on 7/13/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * Convertible to {@link Reader}.
 * @param <Env> Generics parameter.
 * @param <Val> Generics parameter.
 */
public interface ReaderConvertibleType<Env,Val> {
    /**
     * Convert the current {@link ReaderConvertibleType} to {@link Reader}.
     * @return {@link Reader} instance.
     */
    @NotNull Reader<Env,Val> asReader();
}
