package org.swiften.javautilities.functional;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 7/13/17.
 */

/**
 * Implement this interface to provide some {@link Val} instance.
 * @param <Val> Generics parameter.
 */
public interface Supplier<Val> {
    /**
     * Supply some {@link Val} instance.
     * @return {@link Val} instance.
     * @throws Exception If the operation throws.
     */
    @NotNull Val supply() throws Exception;
}
