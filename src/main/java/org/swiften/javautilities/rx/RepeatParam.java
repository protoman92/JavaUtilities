package org.swiften.javautilities.rx;

/**
 * Created by haipham on 22/6/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.protocol.DelayType;
import org.swiften.javautilities.util.Constants;

import java.util.concurrent.TimeUnit;

/**
 * Parameter object for {@link RxUtil#repeatWhile(Flowable, DelayType)}.
 */
public final class RepeatParam implements DelayType {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get default {@link RepeatParam}.
     * @return {@link RepeatParam} instance.
     * @see Builder#withDelay(long)
     * @see Builder#withTimeUnit(TimeUnit)
     */
    @NotNull
    public static RepeatParam defaultInstance() {
        return builder()
            .withDelay(0)
            .withTimeUnit(TimeUnit.MILLISECONDS)
            .build();
    }

    @NotNull private TimeUnit unit;
    private long delay;

    RepeatParam() {
        delay = Constants.DEFAULT_DELAY;
        unit = Constants.DEFAULT_TIME_UNIT;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link Long} value.
     * @see DelayType#delay()
     * @see #delay
     */
    @Override
    public long delay() {
        return delay;
    }

    /**
     * Override this method to provide default implementation.
     * @return {@link TimeUnit} instance.
     * @see DelayType#timeUnit()
     * @see #unit
     */
    @NotNull
    @Override
    public TimeUnit timeUnit() {
        return unit;
    }

    /**
     * Builder class for {@link RepeatParam}.
     */
    public static final class Builder {
        @NotNull
        RepeatParam PARAM;

        Builder() {
            PARAM = new RepeatParam();
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
         * Get {@link #PARAM}.
         * @return {@link RepeatParam} instance.
         * @see #PARAM
         */
        public RepeatParam build() {
            return PARAM;
        }
    }
}
