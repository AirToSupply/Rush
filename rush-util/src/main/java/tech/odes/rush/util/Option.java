package tech.odes.rush.util;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Provides functionality same as java.util.Optional but is also made Serializable. Additional APIs are provided to
 * convert to/from java.util.Optional
 */
public final class Option<T> implements Serializable {

    private static final long serialVersionUID = 0L;

    private static final Option<?> NULL_VAL = new Option<>();

    private final T val;

    public Optional<T> toJavaOptional() {
        return Optional.ofNullable(val);
    }

    /**
     * Convert from java.util.Optional.
     *
     * @param v java.util.Optional object
     * @param <T> type of the value stored in java.util.Optional object
     * @return Option
     */
    public static <T> Option<T> fromJavaOptional(Optional<T> v) {
        return Option.ofNullable(v.orElse(null));
    }

    private Option() {
        this.val = null;
    }

    private Option(T val) {
        if (null == val) {
            throw new NullPointerException("Expected a non-null value. Got null");
        }
        this.val = val;
    }

    public static <T> Option<T> empty() {
        return (Option<T>) NULL_VAL;
    }

    public static <T> Option<T> of(T value) {
        return new Option<>(value);
    }

    public static <T> Option<T> ofNullable(T value) {
        return null == value ? empty() : of(value);
    }

    public boolean isPresent() {
        return null != val;
    }

    public T get() {
        if (null == val) {
            throw new NoSuchElementException("No value present in Option");
        }
        return val;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (val != null) {
            // process the value
            consumer.accept(val);
        }
    }

    public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
        if (null == mapper) {
            throw new NullPointerException("mapper should not be null");
        }
        if (!isPresent()) {
            return empty();
        } else {
            return Option.ofNullable(mapper.apply(val));
        }
    }

    public T orElse(T other) {
        return val != null ? val : other;
    }

    public T orElseGet(Supplier<? extends T> other) {
        return val != null ? val : other.get();
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (val != null) {
            return val;
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Option<?> option = (Option<?>) o;
        return Objects.equals(val, option.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(val);
    }

    @Override
    public String toString() {
        return val != null
                ? "Option{val=" + val + "}"
                : "Optional.empty";
    }
}
