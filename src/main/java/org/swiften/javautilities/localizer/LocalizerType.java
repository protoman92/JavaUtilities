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
     * Localize {@link String} reactively.
     * @param text {@link String} value to be localized.
     * @param locale {@link Locale} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<String> rxLocalize(@NotNull String text,
                                @Nullable Locale locale);

    /**
     * Localize {@link String} reactively. Uses a default {@link Locale}.
     * @param text {@link String} value to be localized.
     * @return {@link Flowable} instance.
     * @see #rxLocalize(String, Locale)
     */
    Flowable<String> rxLocalize(@NotNull String text);

    /**
     * Localize {@link String}.
     * @param text {@link String} value to be localized.
     * @param locale {@link Locale} instance.
     * @return {@link String} value.
     * @see #rxLocalize(String, Locale)
     */
    @NotNull
    String localize(@NotNull String text, @Nullable Locale locale);

    /**
     * Localize {@link String} with a default {@link Locale}.
     * @param text {@link String} value to be localized.
     * @return {@link String} value.
     * @see #localize(String)
     */
    String localize(@NotNull String text);

    /**
     * Localize with format, using {@link LCFormat} instance.
     * @param format {@link LCFormat} instance.
     * @param locale {@link Locale} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<String> rxLocalize(@NotNull LCFormat format,
                                @Nullable Locale locale);

    /**
     * Localize with format without using {@link Locale}.
     * @param format {@link LCFormat} instance.
     * @return {@link Flowable} instance.
     * @see #rxLocalize(LCFormat, Locale)
     */
    @NotNull
    Flowable<String> rxLocalize(@NotNull LCFormat format);

    /**
     * Locale {@link String} with format, using {@link LCFormat}
     * instance.
     * @param format {@link LCFormat} instance.
     * @param locale {@link Locale} instance.
     * @return {@link String} value.
     * @see #rxLocalize(LCFormat, Locale)
     */
    @NotNull
    String localize(@NotNull LCFormat format, @Nullable Locale locale);

    /**
     * Localize {@link String} without using {@link Locale}.
     * @param format {@link LCFormat} instance.
     * @return {@link Locale} instance.
     * @see #localize(LCFormat, Locale)
     */
    @NotNull
    String localize(@NotNull LCFormat format);
}
