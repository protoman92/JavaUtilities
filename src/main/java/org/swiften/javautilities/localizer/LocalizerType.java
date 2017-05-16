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
     * Localize with format, using a {@link LCFormat} instance.
     * @param format A {@link LCFormat} instance.
     * @param locale A {@link Locale} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    Flowable<String> rxLocalize(@NotNull LCFormat format,
                                @Nullable Locale locale);

    /**
     * Localize with format without using a {@link Locale}.
     * @param format A {@link LCFormat} instance.
     * @return A {@link Flowable} instance.
     * @see #rxLocalize(LCFormat, Locale)
     */
    @NotNull
    Flowable<String> rxLocalize(@NotNull LCFormat format);

    /**
     * Locale a {@link String} with format, using a {@link LCFormat}
     * instance.
     * @param format A {@link LCFormat} instance.
     * @param locale A {@link Locale} instance.
     * @return A {@link String} value.
     * @see #rxLocalize(LCFormat, Locale)
     */
    @NotNull
    String localize(@NotNull LCFormat format, @Nullable Locale locale);

    /**
     * Localize a {@link String} without using a {@link Locale}.
     * @param format A {@link LCFormat} instance.
     * @return A {@link Locale} instance.
     * @see #localize(LCFormat, Locale)
     */
    @NotNull
    String localize(@NotNull LCFormat format);
}
