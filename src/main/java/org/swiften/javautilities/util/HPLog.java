package org.swiften.javautilities.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Created by haipham on 3/19/17.
 */
public final class HPLog {
    private static boolean loggingEnabled = true;

    /**
     * Enable or disable logging.
     * @param enabled {@link Boolean} value indicating whether logging is
     *                enabled or not.
     */
    public static void toggleLogging(boolean enabled) {
        loggingEnabled = enabled;
    }

    /**
     * Check if logging is enabled.
     * @return {@link Boolean} value indicating whether logging is enabled.
     */
    public static boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    /**
     * Print new line {@link Object}.
     * @param object {@link Nullable} {@link Object} to be printed.
     * @see #isLoggingEnabled()
     */
    public static void println(@Nullable Object object) {
        if (isLoggingEnabled()) {
            System.out.println(object);
        }
    }

    /**
     * Print {@link Throwable} stack trace.
     * @param t {@link Throwable} instance.
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
     * @see #println(Object)
     */
    public static <T> void println(@NotNull T...objects) {
        println(Arrays.toString(objects));
    }

    /**
     * Print new line with thread information.
     * @param object {@link Object} instance.
     * @see #printft(String, Object...)
     */
    public static void printlnt(@Nullable Object object) {
        printft("%s", object);
    }

    /**
     * Print new line with thread information.
     * @param objects Varargs of {@link Object}.
     * @param <T> Generics parameter.
     * @see #printlnt(Object)
     */
    public static <T> void printlnt(@NotNull T...objects) {
        printlnt(Arrays.toString(objects));
    }

    /**
     * Print format using {@link String} format and varargs arguments.
     * @param format {@link String} value that represents the print format.
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
     * @param format {@link String} value that represents the print format.
     * @param objects A varargs of {@link Object}.
     * @see #isLoggingEnabled()
     * @see #println(Throwable)
     */
    public static void printft(@NotNull String format, @Nullable Object...objects) {
        if (isLoggingEnabled()) {
            String threadFormat = "T%d: %s";
            String log = String.format(format, objects);
            long thread = Thread.currentThread().getId();
            println(String.format(threadFormat, thread, log));
        }
    }

    private HPLog() {}
}
