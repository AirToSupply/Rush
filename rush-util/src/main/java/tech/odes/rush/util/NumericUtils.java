package tech.odes.rush.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import tech.odes.rush.common.exception.RushException;

/**
 * A utility class for numeric.
 */
public class NumericUtils {

    public static String humanReadableByteCount(double bytes) {
        if (bytes < 1024) {
            return String.format("%.1f B", bytes);
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    public static long getMessageDigestHash(final String algorithmName, final String string) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithmName);
        } catch (NoSuchAlgorithmException e) {
            throw new RushException(e);
        }
        return asLong(Objects.requireNonNull(md).digest(string.getBytes(StandardCharsets.UTF_8)));
    }

    public static long asLong(byte[] bytes) {
        ValidationUtils.checkState(bytes.length >= 8, "HashCode#asLong() requires >= 8 bytes.");
        return padToLong(bytes);
    }

    public static long padToLong(byte[] bytes) {
        long retVal = (bytes[0] & 0xFF);
        for (int i = 1; i < Math.min(bytes.length, 8); i++) {
            retVal |= (bytes[i] & 0xFFL) << (i * 8);
        }
        return retVal;
    }
}
