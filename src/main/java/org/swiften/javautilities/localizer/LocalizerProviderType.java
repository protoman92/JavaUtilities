package org.swiften.javautilities.localizer;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haipham on 5/8/17.
 */

/**
 * This interface provides {@link LocalizerType} instance for text
 * localization.
 */
public interface LocalizerProviderType {
    /**
     * Get the associated {@link LocalizerType} instance.
     * @return {@link LocalizerType} instance.
     */
    @NotNull LocalizerType localizer();
}
