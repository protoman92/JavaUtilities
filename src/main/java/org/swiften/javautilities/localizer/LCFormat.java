package org.swiften.javautilities.localizer;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by haipham on 5/14/17.
 */

/**
 * Use this class to implement localization with {@link String} formats, to
 * be used with {@link LocalizerType#rxa_localize(LCFormat, Locale)}.
 */
public class LCFormat {
    /**
     * Get {@link Builder} instance.
     * @return {@link Builder} instance.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Return an empty {@link LCFormat}.
     * @return {@link LCFormat} instance.
     */
    @NotNull
    public static LCFormat empty() {
        return new LCFormat();
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
     * {@link List} of {@link Object} that will be used as arguments
     * by {@link java.text.MessageFormat#format(Object)}
     * @see java.text.MessageFormat#format(Object)
     */
    @NotNull final List<Object> ARGUMENTS;

    LCFormat() {
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
     * @return {@link String} value.
     * @see #pattern
     */
    @NotNull
    public String pattern() {
        return pattern;
    }

    /**
     * Get {@link #ARGUMENTS} as an Array.
     * @return {@link Object} Array.
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
     * Builder class for {@link LCFormat}.
     */
    public static final class Builder {
        @NotNull private final LCFormat FORMAT;

        Builder() {
            FORMAT = new LCFormat();
        }

        /**
         * Set the {@link #pattern} value.
         * @param pattern {@link String} value.
         * @return {@link Builder} instance.
         */
        @NotNull
        public Builder withPattern(@NotNull String pattern) {
            FORMAT.pattern = pattern;
            return this;
        }

        /**
         * Add {@link Object} argument to {@link #ARGUMENTS}. Note that
         * if this argument is {@link String} or another
         * {@link LCFormat}, it will also be localized.
         * @param object {@link Object} instance.
         * @return {@link Builder} instance.
         * @see Collection#add(Object)
         */
        @NotNull
        public Builder addArgument(@NotNull Object object) {
            FORMAT.ARGUMENTS.add(object);
            return this;
        }

        /**
         * Add {@link Collection} of {@link Object} to {@link #ARGUMENTS}.
         * @param args {@link Collection} of {@link Object}.
         * @return {@link Builder} instance.
         * @see Collection#addAll(Collection)
         */
        @NotNull
        public Builder addArguments(@NotNull Collection<Object> args) {
            FORMAT.ARGUMENTS.addAll(args);
            return this;
        }

        @NotNull
        public LCFormat build() {
            return FORMAT;
        }
    }
    //endregion
}
