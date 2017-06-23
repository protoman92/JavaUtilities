package org.swiften.javautilities.protocol;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.util.Constants;

/**
 * Created by haipham on 3/23/17.
 */

/**
 * This interface provides retry count.
 */
public interface RetryProviderType {
    @NotNull RetryProviderType DEFAULT = new RetryProviderType() {
        @Override
        public int retries() {
            return Constants.DEFAULT_RETRIES;
        }
    };

    /**
     * Provide a retry count.
     * @return {@link Integer} value.
     */
    int retries();
}
