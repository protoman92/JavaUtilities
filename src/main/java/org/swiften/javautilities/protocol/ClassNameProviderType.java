package org.swiften.javautilities.protocol;

/**
 * Created by haipham on 5/9/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * This interface provides a class name.
 */
public interface ClassNameProviderType {
    /**
     * Get the associated class name.
     * @return {@link String} value.
     */
    @NotNull String className();
}
