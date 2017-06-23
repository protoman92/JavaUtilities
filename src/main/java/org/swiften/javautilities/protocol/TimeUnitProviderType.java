package org.swiften.javautilities.protocol;

/**
 * Created by haipham on 5/9/17.
 */

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * This interface provides {@link java.util.concurrent.TimeUnit}.
 */
public interface TimeUnitProviderType {
    /**
     * Get the associated {@link TimeUnit} instance.
     * @return {@link TimeUnit} instance.
     */
    @NotNull TimeUnit timeUnit();
}
