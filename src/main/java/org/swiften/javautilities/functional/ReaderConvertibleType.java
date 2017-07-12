package org.swiften.javautilities.functional;

/**
 * Created by haipham on 7/13/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * Convertible to {@link Reader<Env,Val>}.
 * @param <Env> Generics parameter.
 * @param <Val> Generics parameter.
 */
public interface ReaderConvertibleType<Env,Val> {
    /**
     * Convert the current {@link ReaderConvertibleType<Env,Val>} to {@link Reader<Env,Val>}.
     * @return {@link Reader<Env,Val>} instance.
     */
    @NotNull Reader<Env,Val> asReader();
}
