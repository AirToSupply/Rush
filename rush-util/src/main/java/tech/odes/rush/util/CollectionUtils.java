package tech.odes.rush.util;

import java.util.Properties;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tech.odes.rush.util.collection.Pair;

public class CollectionUtils {

    public static boolean elementsEqual(Iterator<?> iterator1, Iterator<?> iterator2) {
        while (iterator1.hasNext()) {
            if (!iterator2.hasNext()) {
                return false;
            }
            Object o1 = iterator1.next();
            Object o2 = iterator2.next();
            if (!Objects.equals(o1, o2)) {
                return false;
            }
        }
        return !iterator2.hasNext();
    }

    @SafeVarargs
    public static <T> Set<T> createSet(final T... elements) {
        return Stream.of(elements).collect(Collectors.toSet());
    }

    public static <K,V> Map<K, V> createImmutableMap(final K key, final V value) {
        return Collections.unmodifiableMap(Collections.singletonMap(key, value));
    }

    @SafeVarargs
    public static <T> List<T> createImmutableList(final T... elements) {
        return Collections.unmodifiableList(Stream.of(elements).collect(Collectors.toList()));
    }

    public static <K,V> Map<K,V> createImmutableMap(final Map<K,V> map) {
        return Collections.unmodifiableMap(map);
    }

    @SafeVarargs
    public static <K,V> Map<K,V> createImmutableMap(final Pair<K,V>... elements) {
        Map<K,V> map = new HashMap<>();
        for (Pair<K,V> pair: elements) {
            map.put(pair.getLeft(), pair.getRight());
        }
        return Collections.unmodifiableMap(map);
    }

    @SafeVarargs
    public static <T> Set<T> createImmutableSet(final T... elements) {
        return Collections.unmodifiableSet(createSet(elements));
    }

    public static <T> Set<T> createImmutableSet(final Set<T> set) {
        return Collections.unmodifiableSet(set);
    }

    public static <T> List<T> createImmutableList(final List<T> list) {
        return Collections.unmodifiableList(list);
    }

    private static Object[] checkElementsNotNull(Object... array) {
        return checkElementsNotNull(array, array.length);
    }

    private static Object[] checkElementsNotNull(Object[] array, int length) {
        for (int i = 0; i < length; i++) {
            checkElementNotNull(array[i], i);
        }
        return array;
    }

    private static Object checkElementNotNull(Object element, int index) {
        return Objects.requireNonNull(element, "Element is null at index " + index);
    }
}
