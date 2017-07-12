package org.swiften.javautilities.functional;

/**
 * Created by haipham on 7/12/17.
 */

/**
 * Convertible to {@link OptionType<Val>}
 * @param <Val> Generics parameter.
 */
public interface OptionConvertibleType<Val> {
    /**
     * Convert the current {@link OptionConvertibleType<Val>} into {@link Option}.
     * @return {@link Option} instance.
     */
    Option<Val> asOption();
}
