package org.swiften.javautilities.protocol;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides a duration and {@link java.util.concurrent.TimeUnit}.
 */
public interface DurationProviderType extends TimeUnitProviderType {
    /**
     * Get a duration.
     * @return {@link Integer} value.
     */
    int duration();
}
