package tech.odes.rush.util;

import javax.annotation.Nullable;

/**
 * Simple utility for operations on strings.
 */
public class StringUtils {

    public static final String EMPTY_STRING = "";

    public static <T> String join(final String... elements) {
        return join(elements, "");
    }

    public static <T> String joinUsingDelim(String delim, final String... elements) {
        return join(elements, delim);
    }

    public static String join(final String[] array, final String separator) {
        if (array == null) {
            return null;
        }
        return org.apache.hadoop.util.StringUtils.join(separator, array);
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }


    /**
     * Returns the given string if it is non-null; the empty string otherwise.
     *
     * @param string the string to test and possibly return
     * @return {@code string} itself if it is non-null; {@code ""} if it is null
     */
    public static String nullToEmpty(@Nullable String string) {
        return string == null ? "" : string;
    }

    public static String objToString(@Nullable Object obj) {
        return obj == null ? null : obj.toString();
    }

    /**
     * Returns the given string if it is nonempty; {@code null} otherwise.
     *
     * @param string the string to test and possibly return
     * @return {@code string} itself if it is nonempty; {@code null} if it is empty or null
     */
    public static @Nullable String emptyToNull(@Nullable String string) {
        return stringIsNullOrEmpty(string) ? null : string;
    }

    private static boolean stringIsNullOrEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }
}
