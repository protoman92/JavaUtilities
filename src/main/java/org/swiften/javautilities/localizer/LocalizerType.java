package org.swiften.javautilities.localizer;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * This interface provides localization capabilities.
 */
public interface LocalizerType extends LocalizeErrorType {
    /**
     * Localize a {@link String} reactively.
     * @param text A {@link String} value to be localized.
     * @param locale A {@link Locale} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<String> rxLocalizeText(@NotNull String text,
                                    @Nullable Locale locale);

    /**
     * Localize a {@link String}.
     * @param text A {@link String} value to be localized.
     * @param locale A {@link Locale} instance.
     * @return A {@link String} value.
     */
    @NotNull
    String localizeText(@NotNull String text, @NotNull Locale locale);
}
