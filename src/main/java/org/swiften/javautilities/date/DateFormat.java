package org.swiften.javautilities.date;

/**
 * Created by haipham on 5/10/17.
 */

import org.jetbrains.annotations.NotNull;

/**
 * Common date formats to be used with {@link java.text.SimpleDateFormat}.
 */
public enum DateFormat {
    dd,
    ddMM,
    ddMMM,
    ddMMMhhmma,
    ddMMMMYYYY,
    EEEEddMMMYYYY,
    EEEEddMMMYYYYhhmma,
    MMMMYYYY;

    /**
     * Get the associated {@link String} format.
     * @return A {@link String} value.
     */
    @NotNull
    public String format() {
        switch (this) {
            case dd:
                return "dd";

            case ddMM:
                return "dd MM";

            case ddMMM:
                return "dd MMM";

            case ddMMMhhmma:
                return "dd MMM hh:mm a";

            case ddMMMMYYYY:
                return "dd MMMM YYYY";

            case EEEEddMMMYYYY:
                return "EEEE, dd MMMM yyyy";

            case EEEEddMMMYYYYhhmma:
                return "EEEE, dd MMMM yyyy hh:mm a";

            case MMMMYYYY:
                return "MMMM, yyyy";

            default:
                return "";
        }
    }
}
