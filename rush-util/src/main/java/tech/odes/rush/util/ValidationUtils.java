package tech.odes.rush.util;

/*
 * Simple utility to test validation conditions (to replace Guava's PreConditions)
 */
public class ValidationUtils {

    public static void checkArgument(final boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Ensures the truth of an expression, throwing the custom errorMessage otherwise.
     *
     * @param expression
     * @param errorMessage
     */
    public static void checkArgument(final boolean expression, final String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * @param expression a boolean expression
     * @throws IllegalStateException if {@code expression} is false
     */
    public static void checkState(final boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param errorMessage - error message
     * @throws IllegalStateException if {@code expression} is false
     */
    public static void checkState(final boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalStateException(errorMessage);
        }
    }
}
