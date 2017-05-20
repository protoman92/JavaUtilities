package org.swiften.javautilities.localizer;

import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import org.swiften.javautilities.collection.CollectionUtil;
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

    Localizer() {
        BUNDLES = new ArrayList<ResourceBundle>();
    }

    //region Getters
    /**
     * Get {@link #BUNDLES}.
     * @return {@link List} of {@link ResourceBundle}.
     * @see #BUNDLES
     */
    @NotNull
    List<ResourceBundle> bundles() {
        return BUNDLES;
    }

    /**
     * Get {@link List} of {@link Locale} from {@link #bundles()}.
     * @return {@link List} of {@link Locale}.
     * @see #bundles()
     * @see ObjectUtil#nonNull(Object)
     */
    @NotNull
    List<Locale> locales() {
        List<ResourceBundle> bundles = bundles();
        List<Locale> locales = new LinkedList<Locale>();

        for (ResourceBundle bundle : bundles) {
            Locale locale;

            if (ObjectUtil.nonNull(bundle, (locale = bundle.getLocale()))) {
                locales.add(locale);
            }
        }

        return locales;
    }
    //endregion

    /**
     * Get {@link Flowable} that emits {@link ResourceBundle} which match a
     * specific {@link Locale} instance.
     * @param FILTER {@link Locale} instance.
     * @return {@link Flowable} instance.
     * @see #bundles()
     */
    @NotNull
    Flowable<ResourceBundle> rxResources(@Nullable final Locale FILTER) {
        return Flowable.fromIterable(bundles())
            .filter(new Predicate<ResourceBundle>() {
                @Override
                public boolean test(@NonNull ResourceBundle bundle) throws Exception {
                    Locale locale = bundle.getLocale();
                    return ObjectUtil.isNull(FILTER, locale) || locale.equals(FILTER);
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
     * @see LocalizerType#rxLocalize(String, Locale)
     * @see #rxResources(Locale)
     * @see #rxGetString(ResourceBundle, String)
     * @see StringUtil#isNotNullOrEmpty(String)
     */
    @NotNull
    public Flowable<String> rxLocalize(@NotNull final String TEXT,
                                       @Nullable Locale locale) {
        return rxResources(locale)
            .flatMap(new Function<ResourceBundle,Publisher<String>>() {
                @NonNull
                @Override
                public Publisher<String> apply(@NotNull ResourceBundle bundle)
                    throws Exception
                {
                    return rxGetString(bundle, TEXT);
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
     * Localize {@link String} with a default {@link Locale}.
     * @param text {@link String} value to be localized.
     * @return {@link Flowable} instance.
     * @see #rxLocalize(String, Locale)
     */
    @NotNull
    public Flowable<String> rxLocalize(@NotNull String text) {
        return rxLocalize(text, null);
    }

    /**
     * Same as above, but blocks.
     * @param text The {@link String} to be localized.
     * @param locale {@link Locale} instance.
     * @return {@link String} value.
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
     * @see ObjectUtil#nonNull(Object...)
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    Flowable<String> rxGetString(@NotNull ResourceBundle bundle,
                                 @NotNull String text) {
        try {
            String string = getString(bundle, text);
            return Flowable.just(string);
        } catch (Exception e) {
            return Flowable.empty();
        }
    }

    /**
     * Acquire a localized text from {@link ResourceBundle} instance.
     * @param bundle {@link ResourceBundle} instance.
     * @param text {@link String} value to be localized.
     * @return {@link String} value.
     * @see ResourceBundle#getString(String)
     * @throws MissingResourceException This is thrown if
     * {@link ResourceBundle#getString(String)} fails.
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
     * @see #rxGetString(ResourceBundle, String)
     * @see StringUtil#isNotNullOrEmpty(String)
     * @see #rxLocalize(LCFormat, Locale)
     */
    @NotNull
    public Flowable<String> rxLocalize(@NotNull final LCFormat FORMAT,
                                       @Nullable final Locale LOCALE) {
        return rxResources(LOCALE)
            .flatMap(new Function<ResourceBundle,Publisher<String>>() {
                @NonNull
                @Override
                public Publisher<String> apply(@NonNull ResourceBundle bundle) throws Exception {
                    return rxGetString(bundle, FORMAT);
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
            .switchIfEmpty(rxLocalize(FORMAT.pattern(), LOCALE));
    }

    /**
     * Same as above, but uses a default {@link Locale} instance.
     * @param format {@link LCFormat} instance.
     * @return {@link Flowable} instance.
     * @see #rxLocalize(LCFormat, Locale)
     */
    @NotNull
    public Flowable<String> rxLocalize(@NotNull LCFormat format) {
        return rxLocalize(format, null);
    }

    /**
     * Localize {@link String} using {@link LCFormat} instance.
     * @param format {@link LCFormat} instance.
     * @param locale {@link Locale} instance.
     * @return {@link String} value.
     * @see #rxLocalize(LCFormat, Locale)
     */
    @NotNull
    public String localize(@NotNull LCFormat format,
                           @Nullable Locale locale) {
        String result = rxLocalize(format, locale).blockingFirst();
        return StringUtil.isNotNullOrEmpty(result) ? result : format.pattern();
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
     * @see #rxGetString(ResourceBundle, String)
     * @see #getString(MessageFormat, Object[])
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    Flowable<String> rxGetString(@NotNull final ResourceBundle BUNDLE,
                                 @NotNull final LCFormat FORMAT) {
        final Locale LOCALE = BUNDLE.getLocale();

        return rxFormatArguments(LOCALE, FORMAT).flatMap(new Function<Object[],Publisher<String>>() {
            @NonNull
            @Override
            public Publisher<String> apply(@NonNull final Object[] ARGS) throws Exception {
                return rxGetTemplate(BUNDLE, FORMAT.pattern())
                    .map(new Function<String, MessageFormat>() {
                        @NonNull
                        @Override
                        public MessageFormat apply(@NonNull String s) throws Exception {
                            return new MessageFormat(s, LOCALE);
                        }
                    })
                    .flatMap(new Function<MessageFormat,Publisher<String>>() {
                        @NonNull
                        @Override
                        public Publisher<String> apply(@NonNull MessageFormat mf) throws Exception {
                            return rxGetString(mf, ARGS);
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
     * @see #rxGetString(ResourceBundle, String)
     */
    @NotNull
    Flowable<String> rxGetTemplate(@NotNull ResourceBundle bundle,
                                   @NotNull String pattern) {
        return rxGetString(bundle, pattern);
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
    Flowable<String> rxGetString(@NotNull MessageFormat format,
                                 @NotNull Object[] args) {
        try {
            String localized = getString(format, args);
            return Flowable.just(localized);
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
     * @throws MissingResourceException This is thrown if
     * {@link MessageFormat#format(Object)} fails.
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
     * @see #rxPrepareArgument(Locale, Object)
     * @see CollectionUtil#toArray(Collection)
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    Flowable<Object[]> rxFormatArguments(@NonNull final Locale LOCALE,
                                         @NotNull LCFormat format) {
        return Flowable.fromArray(format.arguments())
            .flatMap(new Function<Object,Publisher<?>>() {
                @Override
                public Publisher<?> apply(@NonNull Object o) throws Exception {
                    return rxPrepareArgument(LOCALE, o);
                }
            })
            .toList()
            .toFlowable()
            .map(new Function<List<Object>,Object[]>() {
                @Override
                public Object[] apply(@NonNull List<Object> o) throws Exception {
                    return CollectionUtil.toArray(o);
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
     * @see #rxLocalize(LCFormat, Locale)
     * @see #rxLocalize(String, Locale)
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    Flowable<?> rxPrepareArgument(@NonNull Locale locale, @NotNull Object argument) {
        if (argument instanceof LCFormat) {
            return rxLocalize((LCFormat)argument, locale);
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
         * Add {@link ResourceBundle}.
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
            }

            return this;
        }

        @NotNull
        public Localizer build() {
            return LOCALIZER;
        }
    }
}
