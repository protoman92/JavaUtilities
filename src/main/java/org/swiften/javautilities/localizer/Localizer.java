package org.swiften.javautilities.localizer;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import org.swiften.javautilities.collection.CollectionUtil;
import org.swiften.javautilities.collection.Zipped;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.javautilities.string.StringUtil;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created by haipham on 3/25/17.
 */
public class Localizer implements LocalizerType {
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public static Builder builder() {
        return new Builder();
    }

    @NotNull private final List<ResourceBundle> BUNDLES;
    @NotNull private final List<Locale> LOCALES;

    Localizer() {
        BUNDLES = new ArrayList<ResourceBundle>();
        LOCALES = new ArrayList<Locale>();
    }

    //region Getters
    /**
     * Get {@link #BUNDLES}.
     * @return A {@link List} of {@link ResourceBundle}.
     * @see #BUNDLES
     */
    @NotNull
    List<ResourceBundle> bundles() {
        return BUNDLES;
    }

    /**
     * Get {@link #LOCALES}.
     * @return A {@link List} of {@link Locale}.
     * @see #LOCALES
     */
    @NotNull
    List<Locale> locales() {
        return LOCALES;
    }
    //endregion

    /**
     * Get a {@link Flowable} that only emits {@link Zipped} instance of
     * {@link ResourceBundle} and {@link Locale} which matches a specified
     * {@link Locale} instance.
     * @param FILTER A {@link Locale} instance.
     * @return A {@link Flowable} instance.
     * @see #bundles()
     * @see #locales()
     */
    @NotNull
    Flowable<Zipped<ResourceBundle,Locale>> rxResources(@Nullable final Locale FILTER) {
        List<Locale> locales = locales();
        final List<ResourceBundle> BUNDLES = bundles();

        return Flowable.fromIterable(locales)
            .filter(new Predicate<Locale>() {
                @Override
                public boolean test(@NotNull Locale locale) throws Exception {
                    return ObjectUtil.isNull(FILTER) || FILTER.equals(locale);
                }
            })
            .flatMap(new Function<Locale,Publisher<Zipped<ResourceBundle,Locale>>>() {
                @NotNull
                @Override
                public Publisher<Zipped<ResourceBundle,Locale>> apply(
                    @NotNull final Locale LOCALE
                ) throws Exception {
                    return Flowable.fromIterable(BUNDLES)
                        .filter(new Predicate<ResourceBundle>() {
                            @Override
                            public boolean test(@NonNull ResourceBundle bundle)
                                throws Exception
                            {
                                return
                                    ObjectUtil.isNull(FILTER) ||
                                    bundle.getLocale().equals(FILTER);
                            }
                        })
                        .map(new Function<ResourceBundle,Zipped<ResourceBundle,Locale>>() {
                            @NotNull
                            @Override
                            public Zipped<ResourceBundle,Locale> apply(
                                @NotNull ResourceBundle bundle
                            ) throws Exception {
                                return new Zipped<ResourceBundle,Locale>(bundle, LOCALE);
                            }
                        });
                }
            });
    }

    //region Simple localization
    /**
     * Localize a text with the specified {@link #BUNDLES} and
     * {@link #LOCALES}. We can also specify a {@link Locale} to filter out
     * unnecessary {@link Locale}.
     * @param TEXT The {@link String} to be localized.
     * @param locale A {@link Locale} instance.
     * @return A {@link Flowable} instance.
     * @see LocalizerType#rxLocalize(String, Locale)
     * @see #rxResources(Locale)
     * @see #rxGetString(ResourceBundle, Locale, String)
     * @see StringUtil#isNotNullOrEmpty(String)
     */
    @NotNull
    public Flowable<String> rxLocalize(@NotNull final String TEXT,
                                       @Nullable Locale locale) {
        return rxResources(locale)
            .flatMap(new Function<Zipped<ResourceBundle,Locale>,Publisher<String>>() {
                @Override
                public Publisher<String> apply(
                    @NotNull Zipped<ResourceBundle,Locale> zipped
                ) throws Exception {
                    return rxGetString(zipped.FIRST, zipped.SECOND, TEXT);
                }
            })
            .filter(new Predicate<String>() {
                @Override
                public boolean test(@NotNull String s) throws Exception {
                    return StringUtil.isNotNullOrEmpty(s);
                }
            })
            .firstElement()
            .toFlowable()
            .defaultIfEmpty(TEXT);
    }

    /**
     * Localize a {@link String} with a default {@link Locale}.
     * @param text A {@link String} value to be localized.
     * @return A {@link Flowable} instance.
     * @see #rxLocalize(String, Locale)
     */
    @NotNull
    public Flowable<String> rxLocalize(@NotNull String text) {
        return rxLocalize(text, null);
    }

    /**
     * Same as above, but blocks.
     * @param text The {@link String} to be localized.
     * @param locale A {@link Locale} instance.
     * @return A {@link String} value.
     * @see #rxLocalize(String, Locale)
     * @see StringUtil#isNotNullOrEmpty(String)
     */
    @NotNull
    public String localize(@NotNull String text, @Nullable Locale locale) {
        String result = rxLocalize(text, locale).blockingFirst();
        return StringUtil.isNotNullOrEmpty(result) ? result : text;
    }

    /**
     * Same as above, but uses a default {@link Locale}.
     * @param text A {@link String} value to be localized.
     * @return A {@link String} value.
     * @see #localize(String, Locale)
     */
    @NotNull
    public String localize(@NotNull String text) {
        return localize(text, null);
    }

    /**
     * Acquire a localized text reactively. Return an empty {@link Flowable}
     * if an {@link Exception} is thrown.
     * @param bundle A {@link ResourceBundle} instance.
     * @param locale A {@link Locale} instance.
     * @param text A {@link String} value to be localized.
     * @return A {@link Flowable} instance.
     * @see #getString(ResourceBundle, Locale, String)
     * @see ObjectUtil#nonNull(Object...)
     */
    @NotNull
    @SuppressWarnings("all")
    Flowable<String> rxGetString(@Nullable ResourceBundle bundle,
                                 @Nullable Locale locale,
                                 @NotNull String text) {
        if (ObjectUtil.nonNull(bundle, locale)) {
            try {
                String string = getString(bundle, locale, text);
                return Flowable.just(string);
            } catch (Exception e) {}
        }

        return Flowable.empty();
    }

    /**
     * Acquire a localized text from a {@link ResourceBundle} instance.
     * @param bundle A {@link ResourceBundle} instance.
     * @param locale A {@link Locale} instance.
     * @param text A {@link String} value to be localized.
     * @return A {@link String} value.
     * @see ResourceBundle#getString(String)
     * @throws MissingResourceException This is thrown if
     * {@link ResourceBundle#getString(String)} fails.
     */
    @NotNull
    String getString(@NotNull ResourceBundle bundle,
                     @NotNull Locale locale,
                     @NotNull String text) {
        return bundle.getString(text);
    }
    //endregion

    //region Complex localization
    /**
     * Localize reactively with a {@link LocalizationFormat} and a
     * {@link Locale}. This method involves {@link MessageFormat} and works
     * best for format-based localization.
     * @param format A {@link LocalizationFormat} instance.
     * @param locale A {@link Locale} instance.
     * @return A {@link Flowable} instance.
     * @see #rxGetString(ResourceBundle, Locale, LocalizationFormat)
     * @see StringUtil#isNotNullOrEmpty(String)
     */
    @NotNull
    public Flowable<String> rxLocalize(@NotNull final LocalizationFormat format,
                                       @Nullable final Locale locale) {
        return rxResources(locale)
            .flatMap(new Function<Zipped<ResourceBundle,Locale>,Publisher<String>>() {
                @NonNull
                @Override
                public Publisher<String> apply(
                    @NonNull Zipped<ResourceBundle,Locale> zipped
                ) throws Exception {
                    return rxGetString(zipped.FIRST, zipped.SECOND, format);
                }
            })
            .filter(new Predicate<String>() {
                @Override
                public boolean test(@NonNull String s) throws Exception {
                    return StringUtil.isNotNullOrEmpty(s);
                }
            })
            .firstElement()
            .toFlowable()
            .defaultIfEmpty(format.pattern());
    }

    /**
     * Same as above, but uses a default {@link Locale} instance.
     * @param format A {@link LocalizationFormat} instance.
     * @return A {@link Flowable} instance.
     * @see #rxLocalize(LocalizationFormat, Locale)
     */
    @NotNull
    public Flowable<String> rxLocalize(@NotNull LocalizationFormat format) {
        return rxLocalize(format, null);
    }

    /**
     * Localize a {@link String} using a {@link LocalizationFormat} instance.
     * @param format A {@link LocalizationFormat} instance.
     * @param locale A {@link Locale} instance.
     * @return A {@link String} value.
     * @see #rxLocalize(LocalizationFormat, Locale)
     */
    @NotNull
    public String localize(@NotNull LocalizationFormat format,
                           @Nullable Locale locale) {
        String result = rxLocalize(format, locale).blockingFirst();
        return StringUtil.isNotNullOrEmpty(result) ? result : format.pattern();
    }

    /**
     * Same as above, but uses a default {@link Locale}.
     * @param format A {@link LocalizationFormat} instance.
     * @return A {@link String} value.
     * @see #localize(LocalizationFormat, Locale)
     */
    @NotNull
    public String localize(@NotNull LocalizationFormat format) {
        return localize(format, null);
    }

    /**
     * Get a localized {@link String} using a {@link LocalizationFormat}.
     * @param bundle A {@link ResourceBundle} instance.
     * @param LOCALE A {@link Locale} instance.
     * @param format A {@link LocalizationFormat} instance.
     * @return A {@link Flowable} instance.
     * @see ResourceBundle#getString(String)
     * @see MessageFormat#setLocale(Locale)
     * @see MessageFormat#applyPattern(String)
     * @see MessageFormat#format(Object)
     * @see #rxGetString(ResourceBundle, Locale, String)
     * @see #getString(MessageFormat, Object[])
     */
    @NotNull
    @SuppressWarnings("all")
    Flowable<String> rxGetString(@Nullable ResourceBundle bundle,
                                 @Nullable final Locale LOCALE,
                                 @NotNull LocalizationFormat format) {
        if (ObjectUtil.nonNull(bundle, LOCALE)) {
            return rxGetString(bundle, LOCALE, format.pattern())
                .map(new Function<String,MessageFormat>() {
                    @NonNull
                    @Override
                    public MessageFormat apply(@NonNull String s) throws Exception {
                        MessageFormat mf = new MessageFormat("");
                        mf.setLocale(LOCALE);
                        mf.applyPattern(s);
                        return mf;
                    }
                })
                .zipWith(rxFormatArguments(LOCALE, format),
                    new BiFunction<MessageFormat, Object[], String>() {
                        @Override
                        public String apply(@NonNull MessageFormat message,
                                            @NonNull Object[] objects)
                            throws Exception
                        {
                            try {
                                return message.format(objects);
                            } catch (MissingResourceException e) {
                                return "";
                            }
                        }
                    });
        } else {
            return Flowable.empty();
        }
    }

    /**
     * Get a localized {@link String} using an Array of {@link Object}
     * arguments.
     * @param format A {@link LocalizationFormat} instance.
     * @param args A Array of {@link Object}.
     * @return A {@link String} value.
     * @throws MissingResourceException This is thrown if
     * {@link MessageFormat#format(Object)} fails.
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    String getString(@NotNull MessageFormat format, @NotNull Object[] args) {
        return format.format(args);
    }

    /**
     * Get the localization arguments from a {@link LocalizationFormat}. We
     * need to individual prepare each {@link Object} argument in case it
     * is a {@link String} that needs localization, or a nested
     * {@link LocalizationFormat}.
     * @param LOCALE A {@link Locale} instance.
     * @param format A {@link LocalizationFormat} instance.
     * @return A {@link Flowable} instance.
     * @see #rxPrepareArgument(Locale, Object)
     * @see CollectionUtil#toArray(Collection)
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    Flowable<Object[]> rxFormatArguments(@NonNull final Locale LOCALE,
                                         @NotNull LocalizationFormat format) {
        return Flowable.fromArray(format.arguments())
            .flatMap(new Function<Object,Publisher<?>>() {
                @Override
                public Publisher<?> apply(@NonNull Object o) throws Exception {
                    return rxPrepareArgument(LOCALE, o);
                }
            })
            .toList()
            .map(new Function<List<Object>,Object[]>() {
                @Override
                public Object[] apply(@NonNull List<Object> o) throws Exception {
                    return CollectionUtil.toArray(o);
                }
            })
            .toFlowable();
    }

    /**
     * Individually prepare each {@link Object} argument to take care of
     * {@link String} values or {@link LocalizationFormat} instances that
     * need to be localized as well.
     * @param locale A {@link Locale} instance.
     * @param argument An {@link Object} instance.
     * @return A {@link Flowable} instance.
     * @see #rxLocalize(LocalizationFormat, Locale)
     * @see #rxLocalize(String, Locale)
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    Flowable<?> rxPrepareArgument(@NonNull Locale locale, @NotNull Object argument) {
        if (argument instanceof LocalizationFormat) {
            return rxLocalize((LocalizationFormat)argument, locale);
        } else if (argument instanceof String) {
            return rxLocalize((String)argument, locale);
        } else {
            return Flowable.just(argument);
        }
    }
    //endregion

    /**
     * Builder class for {@link Localizer}.
     */
    @SuppressWarnings("WeakerAccess")
    public static final class Builder {
        @NotNull private final Localizer LOCALIZER;

        Builder() {
            LOCALIZER = new Localizer();
        }

        /**
         * Add a {@link ResourceBundle}.
         * @param name The name of the {@link ResourceBundle}.
         * @param locale The {@link Locale} of the {@link ResourceBundle}.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder addBundle(@NotNull String name, @NotNull Locale locale) {
            List<String> prop = ResourceBundle.Control.FORMAT_PROPERTIES;
            ResourceBundle.Control control = ResourceBundle.Control.getNoFallbackControl(prop);
            ResourceBundle bundle = ResourceBundle.getBundle(name, locale, control);

            if (ObjectUtil.nonNull(bundle)) {
                LOCALIZER.BUNDLES.add(bundle);
                LOCALIZER.LOCALES.add(locale);
            }

            return this;
        }

        @NotNull
        public Localizer build() {
            return LOCALIZER;
        }
    }
}
