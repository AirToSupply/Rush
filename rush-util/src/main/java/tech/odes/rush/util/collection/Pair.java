package tech.odes.rush.util.collection;

import java.io.Serializable;
import java.util.Map;

public abstract class Pair<L, R> implements Map.Entry<L, R>, Comparable<Pair<L, R>>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 4954918890077093841L;

    /**
     * <p>
     * Obtains an immutable pair of from two objects inferring the generic types.
     * </p>
     *
     * <p>
     * This factory allows the pair to be created using inference to obtain the generic types.
     * </p>
     *
     * @param <L> the left element type
     * @param <R> the right element type
     * @param left the left element, may be null
     * @param right the right element, may be null
     * @return a pair formed from the two parameters, not null
     */
    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new ImmutablePair<>(left, right);
    }

    // -----------------------------------------------------------------------

    /**
     * <p>
     * Gets the left element from this pair.
     * </p>
     *
     * <p>
     * When treated as a key-value pair, this is the key.
     * </p>
     *
     * @return the left element, may be null
     */
    public abstract L getLeft();

    /**
     * <p>
     * Gets the right element from this pair.
     * </p>
     *
     * <p>
     * When treated as a key-value pair, this is the value.
     * </p>
     *
     * @return the right element, may be null
     */
    public abstract R getRight();

    /**
     * <p>
     * Gets the key from this pair.
     * </p>
     *
     * <p>
     * This method implements the {@code Map.Entry} interface returning the left element as the key.
     * </p>
     *
     * @return the left element as the key, may be null
     */
    @Override
    public final L getKey() {
        return getLeft();
    }

    /**
     * <p>
     * Gets the value from this pair.
     * </p>
     *
     * <p>
     * This method implements the {@code Map.Entry} interface returning the right element as the value.
     * </p>
     *
     * @return the right element as the value, may be null
     */
    @Override
    public R getValue() {
        return getRight();
    }

    // -----------------------------------------------------------------------

    /**
     * <p>
     * Compares the pair based on the left element followed by the right element. The types must be {@code Comparable}.
     * </p>
     *
     * @param other the other pair, not null
     * @return negative if this is less, zero if equal, positive if greater
     */
    @Override
    public int compareTo(final Pair<L, R> other) {

        checkComparable(this);
        checkComparable(other);

        Comparable thisLeft = (Comparable) getLeft();
        Comparable thisRight = (Comparable) getRight();
        Comparable otherLeft = (Comparable) other.getLeft();
        Comparable otherRight = (Comparable) other.getRight();

        if (thisLeft.compareTo(otherLeft) == 0) {
            return thisRight.compareTo(otherRight);
        } else {
            return thisLeft.compareTo(otherLeft);
        }
    }

    /**
     * <p>
     * Compares this pair to another based on the two elements.
     * </p>
     *
     * @param obj the object to compare to, null returns false
     * @return true if the elements of the pair are equal
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Map.Entry<?, ?>) {
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
            return getKey().equals(other.getKey()) && getValue().equals(other.getValue());
        }
        return false;
    }

    /**
     * <p>
     * Returns a suitable hash code. The hash code follows the definition in {@code Map.Entry}.
     * </p>
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        // see Map.Entry API specification
        return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
    }

    /**
     * <p>
     * Returns a String representation of this pair using the format {@code ($left,$right)}.
     * </p>
     *
     * @return a string describing this object, not null
     */
    @Override
    public String toString() {
        return new StringBuilder().append('(').append(getLeft()).append(',').append(getRight()).append(')').toString();
    }

    /**
     * <p>
     * Formats the receiver using the given format.
     * </p>
     *
     * <p>
     * This uses {@link java.util.Formattable} to perform the formatting. Two variables may be used to embed the left and
     * right elements. Use {@code %1$s} for the left element (key) and {@code %2$s} for the right element (value). The
     * default format used by {@code toString()} is {@code (%1$s,%2$s)}.
     * </p>
     *
     * @param format the format string, optionally containing {@code %1$s} and {@code %2$s}, not null
     * @return the formatted string, not null
     */
    public String toString(final String format) {
        return String.format(format, getLeft(), getRight());
    }

    private void checkComparable(Pair<L, R> pair) {
        if (!(pair.getLeft() instanceof Comparable) || !(pair.getRight() instanceof Comparable)) {
            throw new IllegalArgumentException("Elements of Pair must implement Comparable :" + pair);
        }
    }
}

