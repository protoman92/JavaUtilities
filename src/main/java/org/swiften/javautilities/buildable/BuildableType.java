package org.swiften.javautilities.buildable;

/**
 * Created by haipham on 4/1/18.
 */

import org.jetbrains.annotations.NotNull;

/**
 * Implement this interface to provide methods to get {@link Builder} instance.
 * @param <Builder> Generics parameter.
 */
public interface BuildableType<Builder extends BuilderType> {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder instance}.
     */
    @NotNull Builder builder();

    /**
     * Get {@link Builder} instance that has called
     * {@link Builder#withBuildable(BuildableType)} to copy all properties from
     * the current {@link BuildableType}.
     * @return {@link Builder} instance.
     * @see BuilderType#withBuildable(BuildableType).
     */
    @NotNull Builder cloneBuilder();
}
