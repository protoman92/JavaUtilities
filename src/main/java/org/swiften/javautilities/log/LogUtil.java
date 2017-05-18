package org.swiften.javautilities.log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swiften.javautilities.object.ObjectUtil;

import java.util.Arrays;

/**
 * Created by haipham on 3/19/17.
 */
public final class LogUtil {
    private static boolean loggingEnabled = true;

    /**
     * Enable or disable logging.
     * @param enabled A {@link Boolean} value indicating whether logging is
     *                enabled or not.
     */
    public static void toggleLogging(boolean enabled) {
        loggingEnabled = enabled;
    }

    /**
     * Check if logging is enabled.
     * @return A {@link Boolean} value indicating whether logging is enabled.
     */
    public static boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    /**
     * Print new line an {@link Object}.
     * @param object A {@link Nullable} {@link Object} to be printed.
     * @see #isLoggingEnabled()
     */
    public static void println(@Nullable Object object) {
        if (isLoggingEnabled()) {
            System.out.println(object);
        }
    }

    /**
     * Print a {@link Throwable} stack trace.
     * @param t A {@link Throwable} instance.
     * @see #isLoggingEnabled()
     */
    public static void println(@NotNull Throwable t) {
        if (isLoggingEnabled()) {
            t.printStackTrace();
        }
    }

    /**
     * Print new line a varargs of {@link Object}.
     * @param objects A varargs of {@link Object}.
     * @param <T> Generics parameter.
     * @see #isLoggingEnabled()
     */
    public static <T> void println(@NotNull T...objects) {
        if (isLoggingEnabled()) {
            System.out.println(Arrays.toString(objects));
        }
    }

    /**
     * Print format using a {@link String} format and varargs arguments.
     * @param format A {@link String} value that represents the print format.
     * @param objects A varargs of {@link Object}.
     * @see #isLoggingEnabled()
     * @see #println(Throwable)
     */
    public static void printf(@NotNull String format, @Nullable Object...objects) {
        if (isLoggingEnabled()) {
            println(String.format(format, objects));
        }
    }

    /**
     * Print format with a thread id as well.
     * @param format A {@link String} value that represents the print format.
     * @param objects A varargs of {@link Object}.
     * @see #isLoggingEnabled()
     * @see #println(Throwable)
     */
    public static void printfThread(@NotNull String format, @Nullable Object...objects) {
        if (isLoggingEnabled()) {
            String threadFormat = "Thread %d: %s";
            String log = String.format(format, objects);
            long thread = Thread.currentThread().getId();
            println(String.format(threadFormat, thread, log));
        }
    }

    private LogUtil() {}
}
