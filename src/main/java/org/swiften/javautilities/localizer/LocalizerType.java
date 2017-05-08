package org.swiften.javautilities.localizer;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;

/**
 * This interface provides localization capabilities.
 */
public interface LocalizerType extends LocalizeErrorType {
    /**
     * Localize a {@link String} reactively.
     * @param text A {@link String} value to be localized.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<String> rxLocalize(@NotNull String text);

    /**
     * Localize a {@link String}.
     * @param text A {@link String} value to be localized.
     * @return A {@link String} value.
     */
    @NotNull
    String localize(@NotNull String text);
}
