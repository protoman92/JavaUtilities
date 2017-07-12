package org.swiften.javautilities.functional;

/**
 * Created by haipham on 7/12/17.
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implement this interface to signify presence of value or lack thereof.
 * @param <Val> Generics parameter.
 */
public interface MaybeType<Val> {
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
}
