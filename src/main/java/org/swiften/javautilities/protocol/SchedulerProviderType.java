package org.swiften.javautilities.protocol;

import io.reactivex.Scheduler;
import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 23/6/17.
 */
public interface SchedulerProviderType {
    /**
     * Get the associated {@link Scheduler}.
     * @return {@link Scheduler} instance.
     */
    @NotNull Scheduler scheduler();
}
