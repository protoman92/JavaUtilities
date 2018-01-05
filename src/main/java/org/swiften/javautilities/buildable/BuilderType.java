package org.swiften.javautilities.buildable;

/**
 * Created by haipham on 4/1/18.
 */

import org.jetbrains.annotations.NotNull;

/**
 * Implement this interface to provide building method for a {@link BuildableType}.
 * @param <Builder> Generics parameter.
 * @param <Buildable> Generics parameter.
 */
public interface BuilderType<Builder extends BuilderType,Buildable extends BuildableType<Builder>> {
    /**
     * Copy properties from a {@link Buildable} to the inner {@link Buildable}.
     * @param buildable {@link Buildable} instance.
     * @return {@link Builder} instance.
     */
    @NotNull Builder withBuildable(@NotNull Buildable buildable);

    /**
     * Get the inner {@link Buildable}.
     * @return {@link Buildable} instance.
     */
    @NotNull Buildable build();
}