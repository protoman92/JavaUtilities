package org.swiften.javautilities.localizer;

import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import org.swiften.javautilities.collection.HPIterables;
import org.swiften.javautilities.object.HPObjects;
import org.swiften.javautilities.string.HPStrings;
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

    Localizer() {
        BUNDLES = new ArrayList<ResourceBundle>();
    }

    //region Getters
    /**
     * Get {@link #BUNDLES}.
     * @return {@link List} of {@link ResourceBundle}.
     */
    @NotNull
    List<ResourceBundle> bundles() {
        return BUNDLES;
    }

    /**
     * Get {@link List} of {@link Locale} from {@link #bundles()}.
     * @return {@link List} of {@link Locale}.
     */
    @NotNull
    List<Locale> locales() {
        List<ResourceBundle> bundles = bundles();
        List<Locale> locales = new LinkedList<Locale>();

        for (ResourceBundle bundle : bundles) {
            Locale locale;

            if (HPObjects.nonNull(bundle, (locale = bundle.getLocale()))) {
                locales.add(locale);
            }
        }

        return locales;
    }
    //endregion

    /**
     * Get {@link Flowable} that emits {@link ResourceBundle} which match a
     * specific {@link Locale} instance.
     * @param LC {@link Locale} instance.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<ResourceBundle> rxe_resources(@Nullable final Locale LC) {
        return Flowable.fromIterable(bundles())
            .filter(new Predicate<ResourceBundle>() {
                @Override
                public boolean test(@NotNull ResourceBundle bundle) throws Exception {
                    Locale lc = bundle.getLocale();
                    return HPObjects.isNull(LC, lc) || lc.equals(LC);
                }
            });
    }

    //region Simple localization
    /**
     * Localize a text with the specified {@link #BUNDLES}.
     * We can also specify {@link Locale} to filter out unnecessary
     * {@link ResourceBundle}.
     * @param TEXT The {@link String} to be localized.
     * @param locale {@link Locale} instance.
     * @return {@link Flowable} instance.
     * @see LocalizerType#rxa_localize(String, Locale)
     * @see #rxa_getString(ResourceBundle, String)
     * @see #rxe_resources(Locale)
     */
    @NotNull
    public Flowable<String> rxa_localize(@NotNull final String TEXT,
                                         @Nullable Locale locale) {
        return rxe_resources(locale)
            .flatMap(new Function<ResourceBundle,Publisher<String>>() {
                @NotNull
                @Override
                public Publisher<String> apply(@NotNull ResourceBundle bundle) throws Exception {
                    return rxa_getString(bundle, TEXT);
                }
            })
            .filter(new Predicate<String>() {
                @Override
                public boolean test(@NotNull String s) throws Exception {
                    return HPStrings.isNotNullOrEmpty(s);
                }
            })
            .firstElement()
            .toFlowable()
            .defaultIfEmpty(TEXT);
    }

    /**
     * Localize {@link String} with a default {@link Locale}.
     * @param text {@link String} value to be localized.
     * @return {@link Flowable} instance.
     * @see #rxa_localize(String, Locale)
     */
    @NotNull
    public Flowable<String> rxa_localize(@NotNull String text) {
        return rxa_localize(text, null);
    }

    /**
     * Same as above, but blocks.
     * @param text The {@link String} to be localized.
     * @param locale {@link Locale} instance.
     * @return {@link String} value.
     * @see HPStrings#isNotNullOrEmpty(String)
     * @see #rxa_localize(String, Locale)
     */
    @NotNull
    public String localize(@NotNull String text, @Nullable Locale locale) {
        String result = rxa_localize(text, locale).blockingFirst();
        return HPStrings.isNotNullOrEmpty(result) ? result : text;
    }

    /**
     * Same as above, but uses a default {@link Locale}.
     * @param text {@link String} value to be localized.
     * @return {@link String} value.
     * @see #localize(String, Locale)
     */
    @NotNull
    public String localize(@NotNull String text) {
        return localize(text, null);
    }

    /**
     * Acquire a localized text reactively. Return an empty {@link Flowable}
     * if {@link Exception} is thrown.
     * @param bundle {@link ResourceBundle} instance.
     * @param text {@link String} value to be localized.
     * @return {@link Flowable} instance.
     * @see #getString(MessageFormat, Object[])
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    Flowable<String> rxa_getString(@NotNull ResourceBundle bundle, @NotNull String text) {
        try {
            return Flowable.just(getString(bundle, text));
        } catch (Exception e) {
            return Flowable.empty();
        }
    }

    /**
     * Acquire a localized text from {@link ResourceBundle} instance.
     * @param bundle {@link ResourceBundle} instance.
     * @param text {@link String} value to be localized.
     * @return {@link String} value.
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    String getString(@NotNull ResourceBundle bundle, @NotNull String text) {
        return bundle.getString(text);
    }
    //endregion

    //region Complex localization
    /**
     * Localize reactively with {@link LCFormat} and a
     * {@link Locale}. This method involves {@link MessageFormat} and works
     * best for format-based localization.
     * @param FORMAT {@link LCFormat} instance.
     * @param LOCALE {@link Locale} instance.
     * @return {@link Flowable} instance.
     * @see #rxa_getString(ResourceBundle, String)
     * @see #rxa_localize(LCFormat, Locale)
     */
    @NotNull
    public Flowable<String> rxa_localize(@NotNull final LCFormat FORMAT,
                                         @Nullable final Locale LOCALE) {
        final Localizer THIS = this;

        return rxe_resources(LOCALE)
            .flatMap(new Function<ResourceBundle,Publisher<String>>() {
                @NotNull
                @Override
                public Publisher<String> apply(@NotNull ResourceBundle bundle) throws Exception {
                    return THIS.rxa_getString(bundle, FORMAT);
                }
            })
            .filter(new Predicate<String>() {
                @Override
                public boolean test(@NotNull String s) throws Exception {
                    return HPStrings.isNotNullOrEmpty(s);
                }
            })
            .firstElement()
            .toFlowable()
            .switchIfEmpty(rxa_localize(FORMAT.pattern(), LOCALE));
    }

    /**
     * Same as above, but uses a default {@link Locale} instance.
     * @param format {@link LCFormat} instance.
     * @return {@link Flowable} instance.
     * @see #rxa_localize(LCFormat, Locale)
     */
    @NotNull
    public Flowable<String> rxa_localize(@NotNull LCFormat format) {
        return rxa_localize(format, null);
    }

    /**
     * Localize {@link String} using {@link LCFormat} instance.
     * @param format {@link LCFormat} instance.
     * @param locale {@link Locale} instance.
     * @return {@link String} value.
     * @see #rxa_localize(LCFormat, Locale)
     */
    @NotNull
    public String localize(@NotNull LCFormat format,
                           @Nullable Locale locale) {
        String result = rxa_localize(format, locale).blockingFirst();
        return HPStrings.isNotNullOrEmpty(result) ? result : format.pattern();
    }

    /**
     * Same as above, but uses a default {@link Locale}.
     * @param format {@link LCFormat} instance.
     * @return {@link String} value.
     * @see #localize(LCFormat, Locale)
     */
    @NotNull
    public String localize(@NotNull LCFormat format) {
        return localize(format, null);
    }

    /**
     * Get a localized {@link String} using {@link LCFormat}.
     * @param BUNDLE {@link ResourceBundle} instance.
     * @param FORMAT {@link LCFormat} instance.
     * @return {@link Flowable} instance.
     * @see ResourceBundle#getString(String)
     * @see MessageFormat#setLocale(Locale)
     * @see MessageFormat#applyPattern(String)
     * @see MessageFormat#format(Object)
     * @see #rxa_getString(ResourceBundle, String)
     * @see #getString(MessageFormat, Object[])
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    Flowable<String> rxa_getString(@NotNull final ResourceBundle BUNDLE,
                                   @NotNull final LCFormat FORMAT) {
        final Locale LOCALE = BUNDLE.getLocale();

        return rxa_formatArguments(LOCALE, FORMAT).flatMap(new Function<Object[],Publisher<String>>() {
            @NotNull
            @Override
            public Publisher<String> apply(@NotNull final Object[] ARGS) throws Exception {
                return rxa_getTemplate(BUNDLE, FORMAT.pattern())
                    .map(new Function<String, MessageFormat>() {
                        @NotNull
                        @Override
                        public MessageFormat apply(@NotNull String s) throws Exception {
                            return new MessageFormat(s, LOCALE);
                        }
                    })
                    .flatMap(new Function<MessageFormat,Publisher<String>>() {
                        @NotNull
                        @Override
                        public Publisher<String> apply(@NotNull MessageFormat mf) throws Exception {
                            return rxa_getString(mf, ARGS);
                        }
                    });
            }
        });
    }

    /**
     * Get the template pattern to be used with
     * {@link MessageFormat#applyPattern(String)}.
     * @param bundle {@link ResourceBundle} instance.
     * @param pattern {@link String} value.
     * @return {@link Flowable} instance.
     * @see MessageFormat#applyPattern(String)
     * @see #rxa_getString(ResourceBundle, String)
     */
    @NotNull
    Flowable<String> rxa_getTemplate(@NotNull ResourceBundle bundle,
                                     @NotNull String pattern) {
        return rxa_getString(bundle, pattern);
    }

    /**
     * Get a localized {@link String} using an Array of {@link Object}
     * arguments.
     * @param format {@link LCFormat} instance.
     * @param args A Array of {@link Object}.
     * @return {@link Flowable} instance.
     * @see #getString(MessageFormat, Object[])
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    Flowable<String> rxa_getString(@NotNull MessageFormat format,
                                   @NotNull Object[] args) {
        try {
            return Flowable.just(getString(format, args));
        } catch (MissingResourceException e) {
            return Flowable.empty();
        }
    }

    /**
     * Get a localized {@link String} using an Array of {@link Object}
     * arguments.
     * @param format {@link LCFormat} instance.
     * @param args A Array of {@link Object}.
     * @return {@link String} value.
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    String getString(@NotNull MessageFormat format, @NotNull Object[] args) {
        return format.format(args);
    }

    /**
     * Get the localization arguments from {@link LCFormat}. We
     * need to individual prepare each {@link Object} argument in case it
     * is {@link String} that needs localization, or a nested
     * {@link LCFormat}.
     * @param LOCALE {@link Locale} instance.
     * @param format {@link LCFormat} instance.
     * @return {@link Flowable} instance.
     * @see #rxa_prepareArgument(Locale, Object)
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    Flowable<Object[]> rxa_formatArguments(@NotNull final Locale LOCALE,
                                           @NotNull LCFormat format) {
        return Flowable.fromArray(format.arguments())
            .flatMap(new Function<Object,Publisher<?>>() {
                @Override
                public Publisher<?> apply(@NotNull Object o) throws Exception {
                    return rxa_prepareArgument(LOCALE, o);
                }
            })
            .toList()
            .toFlowable()
            .map(new Function<List<Object>,Object[]>() {
                @Override
                public Object[] apply(@NotNull List<Object> o) throws Exception {
                    return HPIterables.toArray(o);
                }
            });
    }

    /**
     * Individually prepare each {@link Object} argument to take care of
     * {@link String} values or {@link LCFormat} instances that
     * need to be localized as well.
     * @param locale {@link Locale} instance.
     * @param argument {@link Object} instance.
     * @return {@link Flowable} instance.
     * @see #rxa_localize(LCFormat, Locale)
     * @see #rxa_localize(String, Locale)
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    Flowable<?> rxa_prepareArgument(@NotNull Locale locale, @NotNull Object argument) {
        if (argument instanceof LCFormat) {
            return rxa_localize((LCFormat)argument, locale);
        } else if (argument instanceof String) {
            return rxa_localize((String)argument, locale);
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
         * Add {@link ResourceBundle}.
         * @param name The name of the {@link ResourceBundle}.
         * @param locale The {@link Locale} of the {@link ResourceBundle}.
         * @return {@link Builder} instance.
         * @see #BUNDLES
         */
        @NotNull
        public Builder addBundle(@NotNull String name, @NotNull Locale locale) {
            List<String> prop = ResourceBundle.Control.FORMAT_PROPERTIES;
            ResourceBundle.Control control = ResourceBundle.Control.getNoFallbackControl(prop);
            ResourceBundle bundle = ResourceBundle.getBundle(name, locale, control);

            if (HPObjects.nonNull(bundle)) {
                LOCALIZER.BUNDLES.add(bundle);
            }

            return this;
        }

        @NotNull
        public Localizer build() {
            return LOCALIZER;
        }
    }
}
