package tech.odes.rush.util.collection;

public final class ImmutablePair<L, R> extends Pair<L, R> {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 4954918890077093841L;

    /**
     * Left object.
     */
    public final L left;

    /**
     * Right object.
     */
    public final R right;

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
    public static <L, R> ImmutablePair<L, R> of(final L left, final R right) {
        return new ImmutablePair<L, R>(left, right);
    }

    /**
     * Create a new pair instance.
     *
     * @param left the left value, may be null
     * @param right the right value, may be null
     */
    public ImmutablePair(final L left, final R right) {
        super();
        this.left = left;
        this.right = right;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public L getLeft() {
        return left;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R getRight() {
        return right;
    }

    /**
     * <p>
     * Throws {@code UnsupportedOperationException}.
     * </p>
     *
     * <p>
     * This pair is immutable, so this operation is not supported.
     * </p>
     *
     * @param value the value to set
     * @return never
     * @throws UnsupportedOperationException as this operation is not supported
     */
    @Override
    public R setValue(final R value) {
        throw new UnsupportedOperationException();
    }

}
