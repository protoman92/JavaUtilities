package org.swiften.javautilities.protocol;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides methods to repeat an action.
 */
public interface RepeatType extends DelayType {
    /**
     * Get the number of times to repeat an action.
     * @return {@link Integer} value.
     */
    int times();

    /**
     * Get the delay duration between every two actions.
     * @return {@link Long} value.
     */
    long delay();
}
