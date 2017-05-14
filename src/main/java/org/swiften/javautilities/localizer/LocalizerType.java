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
    Flowable<String> rxLocalize(@NotNull String text,
                                @Nullable Locale locale);

    /**
     * Localize a {@link String} reactively. Uses a default {@link Locale}.
     * @param text A {@link String} value to be localized.
     * @return A {@link Flowable} instance.
     * @see #rxLocalize(String, Locale)
     */
    Flowable<String> rxLocalize(@NotNull String text);

    /**
     * Localize a {@link String}.
     * @param text A {@link String} value to be localized.
     * @param locale A {@link Locale} instance.
     * @return A {@link String} value.
     * @see #rxLocalize(String, Locale)
     */
    @NotNull
    String localize(@NotNull String text, @Nullable Locale locale);

    /**
     * Localize a {@link String} with a default {@link Locale}.
     * @param text A {@link String} value to be localized.
     * @return A {@link String} value.
     * @see #localize(String)
     */
    String localize(@NotNull String text);

    /**
     * Localize with format, using a {@link LocalizationFormat} instance.
     * @param format A {@link LocalizationFormat} instance.
     * @param locale A {@link Locale} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<String> rxLocalize(@NotNull LocalizationFormat format,
                                @Nullable Locale locale);

    /**
     * Localize with format without using a {@link Locale}.
     * @param format A {@link LocalizationFormat} instance.
     * @return A {@link Flowable} instance.
     * @see #rxLocalize(LocalizationFormat, Locale)
     */
    @NotNull
    Flowable<String> rxLocalize(@NotNull LocalizationFormat format);

    /**
     * Locale a {@link String} with format, using a {@link LocalizationFormat}
     * instance.
     * @param format A {@link LocalizationFormat} instance.
     * @param locale A {@link Locale} instance.
     * @return A {@link String} value.
     * @see #rxLocalize(LocalizationFormat, Locale)
     */
    @NotNull
    String localize(@NotNull LocalizationFormat format, @Nullable Locale locale);

    /**
     * Localize a {@link String} without using a {@link Locale}.
     * @param format A {@link LocalizationFormat} instance.
     * @return A {@link Locale} instance.
     * @see #localize(LocalizationFormat, Locale)
     */
    @NotNull
    String localize(@NotNull LocalizationFormat format);
}
