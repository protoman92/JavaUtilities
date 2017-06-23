package org.swiften.javautilities.util;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.protocol.DelayProviderType;
import org.swiften.javautilities.protocol.RetryProviderType;
import org.swiften.javautilities.protocol.SchedulerProviderType;
import org.swiften.javautilities.protocol.TimeUnitProviderType;

import java.util.concurrent.TimeUnit;

/**
 * Created by haipham on 16/6/17.
 */
public final class Constants {
    /**
     * This value will be used to set default retry count for
     * {@link RetryProviderType#retries()}
     */
    public static int DEFAULT_RETRIES = 2;

    /**
     * This value will be used to set default delay for
     * {@link DelayProviderType}.
     */
    public static long DEFAULT_DELAY = 1000;

    /**
     * This value will be used to set default {@link TimeUnit} for
     * {@link TimeUnitProviderType#timeUnit()}.
     */
    @NotNull
    public static TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;

    /**
     * This value will be used to set default {@link Scheduler} for
     * {@link SchedulerProviderType#scheduler()}.
     */
    @NotNull
    public static Scheduler DEFAULT_SCHEDULER = Schedulers.computation();
}
