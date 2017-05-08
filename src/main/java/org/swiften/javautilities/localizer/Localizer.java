package org.swiften.javautilities.localizer;

import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.javautilities.string.StringUtil;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import org.jetbrains.annotations.NotNull;

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

    /**
     * Get {@link #BUNDLES}.
     * @return A {@link List} of {@link ResourceBundle}.
     */
    @NotNull
    public List<ResourceBundle> bundles() {
        return BUNDLES;
    }

    /**
     * Get {@link #LOCALES}.
     * @return A {@link List} of {@link Locale}.
     */
    @NotNull
    public List<Locale> locales() {
        return LOCALES;
    }

    /**
     * Localize a text with the specified {@link #BUNDLES} and
     * {@link #LOCALES}.
     * @param TEXT The {@link String} to be localized.
     * @return A {@link Flowable} instance.
     * @see #getString(ResourceBundle, String)
     */
    @NotNull
    public Flowable<String> rxLocalize(@NotNull final String TEXT) {
        final List<ResourceBundle> BUNDLES = bundles();
        final List<Locale> LOCALES = locales();
        final int LENGTH = LOCALES.size();

        class Localize {
            @NotNull
            @SuppressWarnings("WeakerAccess")
            Flowable<String> localize(final int INDEX) {
                if (INDEX < LENGTH) {
                    Locale.setDefault(LOCALES.get(INDEX));

                    return Flowable.fromIterable(BUNDLES)
                        .map(new Function<ResourceBundle,String>() {
                            @NonNull
                            @Override
                            public String apply(@NonNull ResourceBundle bundle)
                                throws Exception
                            {
                                return getString(bundle, TEXT);
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
                        .switchIfEmpty(new Localize().localize(INDEX + 1));
                }

                return Flowable.empty();
            }
        }

        return new Localize().localize(0)
            .filter(new Predicate<String>() {
                @Override
                public boolean test(@NonNull String s) throws Exception {
                    return StringUtil.isNotNullOrEmpty(s);
                }
            })
            .defaultIfEmpty(TEXT);
    }

    /**
     * Same as above, but blocks.
     * @param text The {@link String} to be localized.
     * @return A {@link String} value.
     * @see #rxLocalize(String)
     */
    @NotNull
    public String localize(@NotNull String text) {
        String result = rxLocalize(text).blockingSingle();
        return StringUtil.isNotNullOrEmpty(result) ? result : text;
    }

    @NotNull
    public String getString(@NotNull ResourceBundle bundle, @NotNull String text) {
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
            ResourceBundle bundle = ResourceBundle.getBundle(name, locale);

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
