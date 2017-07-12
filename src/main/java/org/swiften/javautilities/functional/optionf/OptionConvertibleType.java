package org.swiften.javautilities.functional.optionf;

/**
 * Created by haipham on 7/12/17.
 */

/**
 * Convertible to {@link OptionType<Val>}
 * @param <Val> Generics parameter.
 */
public interface OptionConvertibleType<Val> {
    /**
     * Convert the current {@link OptionConvertibleType<Val>} into {@link Option<Val>}.
     * @return {@link Option<Val>} instance.
     */
    Option<Val> asOption();
}
