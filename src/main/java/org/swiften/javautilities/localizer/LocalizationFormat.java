package org.swiften.javautilities.localizer;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by haipham on 5/14/17.
 */

/**
 * Use this class to implement localization with {@link String} formats, to
 * be used with {@link LocalizerType#rxLocalize(LocalizationFormat, Locale)}.
 */
public class LocalizationFormat {
    /**
     * Get a {@link Builder} instance.
     * @return A {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * This variable should be the key identifier that can be used by a
     * {@link java.util.ResourceBundle} to get a value. This value will then
     * be used by {@link java.text.MessageFormat#applyPattern(String)} to
     * acquire the format {@link String}.
     * @see java.util.ResourceBundle#getString(String)
     * @see java.text.MessageFormat#applyPattern(String)
     */
    @NotNull String pattern;

    /**
     * A {@link List} of {@link Object} that will be used as arguments
     * by {@link java.text.MessageFormat#format(Object)}
     * @see java.text.MessageFormat#format(Object)
     */
    @NotNull final List<Object> ARGUMENTS;

    LocalizationFormat() {
        ARGUMENTS = new LinkedList<Object>();
        pattern = "";
    }

    @Override
    public String toString() {
        return String.format(
            "Pattern: %s, arguments: %s",
            pattern(),
            Arrays.toString(arguments()));
    }

    //region Getters
    /**
     * Get {@link #pattern}.
     * @return A {@link String} value.
     * @see #pattern
     */
    @NotNull
    public String pattern() {
        return pattern;
    }

    /**
     * Get {@link #ARGUMENTS} as an Array.
     * @return An {@link Object} Array.
     * @see #ARGUMENTS
     */
    @NotNull
    public Object[] arguments() {
        int length = ARGUMENTS.size();
        return ARGUMENTS.toArray(new Object[length]);
    }
    //endregion

    //region Builder
    /**
     * Builder class for {@link LocalizationFormat}.
     */
    public static final class Builder {
        @NotNull private final LocalizationFormat FORMAT;

        Builder() {
            FORMAT = new LocalizationFormat();
        }

        /**
         * Set the {@link #pattern} value.
         * @param pattern A {@link String} value.
         * @return The current {@link Builder} instance.
         */
        @NotNull
        public Builder withPattern(@NotNull String pattern) {
            FORMAT.pattern = pattern;
            return this;
        }

        /**
         * Add an {@link Object} argument to {@link #ARGUMENTS}. Note that
         * if this argument is a {@link String} or another
         * {@link LocalizationFormat}, it will also be localized.
         * @param object An {@link Object} instance.
         * @return The current {@link Builder} instance.
         * @see Collection#add(Object)
         */
        @NotNull
        public Builder addArgument(@NotNull Object object) {
            FORMAT.ARGUMENTS.add(object);
            return this;
        }

        /**
         * Add a {@link Collection} of {@link Object} to {@link #ARGUMENTS}.
         * @param args An {@link Collection} of {@link Object}.
         * @return The current {@link Builder} instance.
         * @see Collection#addAll(Collection)
         */
        @NotNull
        public Builder addArguments(@NotNull Collection<Object> args) {
            FORMAT.ARGUMENTS.addAll(args);
            return this;
        }

        @NotNull
        public LocalizationFormat build() {
            return FORMAT;
        }
    }
    //endregion
}
