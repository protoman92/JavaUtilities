package org.swiften.javautilities.localizer;

import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import org.swiften.javautilities.collection.Zipped;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.javautilities.string.StringUtil;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
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
     * @param LOCALE A {@link Locale} instance.
     * @return A {@link Flowable} instance.
     * @see #bundles()
     * @see #locales()
     */
    @NotNull
    Flowable<Zipped<ResourceBundle,Locale>> rxLocalizationResources(
        @Nullable final Locale LOCALE
    ) {
        final List<ResourceBundle> BUNDLES = bundles();

        return Flowable.fromIterable(locales())
            .filter(new Predicate<Locale>() {
                @Override
                public boolean test(@NonNull Locale locale) throws Exception {
                    return ObjectUtil.isNull(LOCALE) || LOCALE.equals(locale);
                }
            })
            .flatMap(new Function<Locale,Publisher<Zipped<ResourceBundle,Locale>>>() {
                @Override
                public Publisher<Zipped<ResourceBundle,Locale>> apply(
                    @NonNull final Locale LOCALE
                ) throws Exception {
                    return Flowable.fromIterable(BUNDLES)
                        .map(new Function<ResourceBundle,Zipped<ResourceBundle,Locale>>() {
                            @Override
                            public Zipped<ResourceBundle,Locale> apply(
                                @NonNull ResourceBundle bundle
                            ) throws Exception {
                                return new Zipped<ResourceBundle,Locale>(bundle, LOCALE);
                            }
                        });
                }
            });
    }

    /**
     * Localize a text with the specified {@link #BUNDLES} and
     * {@link #LOCALES}. We can also specify a {@link Locale} to filter out
     * unnecessary {@link Locale}.
     * @param TEXT The {@link String} to be localized.
     * @param locale A {@link Locale} instance.
     * @return A {@link Flowable} instance.
     * @see LocalizerType#rxLocalizeText(String, Locale)
     * @see #rxLocalizationResources(Locale)
     * @see #rxGetString(ResourceBundle, Locale, String)
     */
    @NotNull
    public Flowable<String> rxLocalizeText(@NotNull final String TEXT,
                                           @Nullable Locale locale) {
        return rxLocalizationResources(locale)
            .flatMap(new Function<Zipped<ResourceBundle,Locale>,Publisher<String>>() {
                @Override
                public Publisher<String> apply(
                    @NonNull Zipped<ResourceBundle,Locale> zipped
                ) throws Exception {
                    return rxGetString(zipped.FIRST, zipped.SECOND, TEXT);
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
            .defaultIfEmpty(TEXT);
    }

    /**
     * Same as above, but blocks.
     * @param text The {@link String} to be localized.
     * @param locale A {@link Locale} instance.
     * @return A {@link String} value.
     * @see #rxLocalizeText(String, Locale)
     */
    @NotNull
    public String localizeText(@NotNull String text, @NotNull Locale locale) {
        String result = rxLocalizeText(text, locale).blockingSingle();
        return StringUtil.isNotNullOrEmpty(result) ? result : text;
    }

    /**
     * Acquire a localized text reactively. Return an empty {@link Flowable}
     * if an {@link Exception} is thrown.
     * @param bundle A {@link ResourceBundle} instance.
     * @param locale A {@link Locale} instance.
     * @param text A {@link String} value to be localized.
     * @return A {@link Flowable} instance.
     * @see #getString(ResourceBundle, Locale, String)
     */
    @NotNull
    @SuppressWarnings("EmptyCatchBlock")
    Flowable<String> rxGetString(@Nullable ResourceBundle bundle,
                                 @Nullable Locale locale,
                                 @NotNull String text) {
        if (ObjectUtil.nonNull(bundle) && ObjectUtil.nonNull(locale)) {
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
     */
    @NotNull
    String getString(@NotNull ResourceBundle bundle,
                     @NotNull Locale locale,
                     @NotNull String text) {
        return bundle.getString(text);
    }

    /**
     * Builder class for {@link Localizer}.
     */
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
            ResourceBundle.Control control = ResourceBundle.Control
                .getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

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
