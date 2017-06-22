package org.swiften.javautilities.protocol;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.util.Constants;

/**
 * Created by haipham on 3/23/17.
 */

/**
 * This interface provides retry count.
 */
public interface RetryType {
    @NotNull RetryType DEFAULT = new RetryType() {
        @Override
        public int retries() {
            return Constants.DEFAULT_RETRIES;
        }
    };

    /**
     * Use this retry count if we are running an operation that is not expected
     * to throw {@link Exception}.
     * @return {@link Integer} value.
     */
    int retries();
}
