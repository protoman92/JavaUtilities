package org.swiften.javautilities.rx;

/**
 * Created by haipham on 22/6/17.
 */

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.protocol.DelayProviderType;
import org.swiften.javautilities.protocol.SchedulerProviderType;
import org.swiften.javautilities.util.Constants;

import java.util.concurrent.TimeUnit;

/**
 * Parameter object for {@link RxUtil#repeatWhile(Flowable, DelayProviderType)}.
 */
public final class RxUtilParam implements DelayProviderType, SchedulerProviderType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get default {@link RxUtilParam}.
     * @return {@link RxUtilParam} instance.
     * @see Builder#withDelay(long)
     * @see Builder#withTimeUnit(TimeUnit)
     */
    @NotNull
    public static RxUtilParam defaultInstance() {
        return builder()
            .withDelay(0)
            .withTimeUnit(TimeUnit.MILLISECONDS)
            .withScheduler(Schedulers.trampoline())
            .build();
    }

    @NotNull private TimeUnit unit;
    @NotNull private Scheduler scheduler;
    private long delay;

    RxUtilParam() {
        delay = Constants.DEFAULT_DELAY;
        scheduler = Constants.DEFAULT_SCHEDULER;
        unit = Constants.DEFAULT_TIME_UNIT;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Long} value.
     * @see DelayProviderType#delay()
     * @see #delay
     */
    @Override
    public long delay() {
        return delay;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link TimeUnit} instance.
     * @see DelayProviderType#timeUnit()
     * @see #unit
     */
    @NotNull
    @Override
    public TimeUnit timeUnit() {
        return unit;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Scheduler} instance.
     * @see SchedulerProviderType#scheduler()
     * @see #scheduler
     */
    @NotNull
    @Override
    public Scheduler scheduler() {
        return scheduler;
    }

    /**
     * Builder class for {@link RxUtilParam}.
     */
    public static final class Builder {
        @NotNull
        RxUtilParam PARAM;

        Builder() {
            PARAM = new RxUtilParam();
        }

        /**
         * Set {@link #delay}.
         * @param delay {@link Long} value.
         * @return {@link Builder} instance.
         * @see #delay
         */
        @NotNull
        public Builder withDelay(long delay) {
            PARAM.delay = delay;
            return this;
        }

        /**
         * Set the {@link #unit} instance.
         * @param unit {@link TimeUnit} instance.
         * @return {@link Builder} instance.
         * @see #unit
         */
        @NotNull
        public Builder withTimeUnit(@NotNull TimeUnit unit) {
            PARAM.unit = unit;
            return this;
        }

        /**
         * Set the {@link #scheduler} instance.
         * @param scheduler {@link Scheduler} instance.
         * @return {@link Builder} instance.
         * @see #scheduler
         */
        @NotNull
        public Builder withScheduler(@NotNull Scheduler scheduler) {
            PARAM.scheduler = scheduler;
            return this;
        }

        /**
         * Get {@link #PARAM}.
         * @return {@link RxUtilParam} instance.
         * @see #PARAM
         */
        public RxUtilParam build() {
            return PARAM;
        }
    }
}
