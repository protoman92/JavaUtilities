package org.swiften.javautilities.protocol;

/**
 * Created by haipham on 5/9/17.
 */

/**
 * This interface provides delay and {@link java.util.concurrent.TimeUnit}.
 */
public interface DelayProviderType extends TimeUnitProviderType {
    /**
     * Get the associated delay duration.
     * @return {@link Long} value.
     */
    long delay();
}
