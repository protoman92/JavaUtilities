package org.swiften.javautilities.util;

import org.swiften.javautilities.protocol.RetryType;
import org.swiften.javautilities.protocol.TimeUnitType;

import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 16/6/17.
 */
public final class Constants {
    /**
     * This value will be used to set default retry count for
     * {@link RetryType#retries()}
     */
    public static int DEFAULT_RETRIES = 2;

    /**
     * This value will be used to set default delay for
     * {@link org.swiften.javautilities.protocol.DelayType}.
     */
    public static long DEFAULT_DELAY = 1000;

    /**
     * This value will be used to set default {@link TimeUnit} for
     * {@link TimeUnitType#timeUnit()}
     */
    public static TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
}
