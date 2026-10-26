package dev.m6fgr.epic_api.cls.lists;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

public record EnumArray<T extends Enum<T>>(T... elements) implements Iterable<T> {
    @SafeVarargs
    public EnumArray {}

    public static <T extends Enum<T>> EnumArray<T> of(T[] elements) {
        return new EnumArray<>(elements);
    }

    public EnumArray<T> filter(Predicate<T> predicate) {
        @SuppressWarnings("unchecked")
        T[] filtered = Arrays.stream(this.elements)
                .filter(predicate)
                .toArray(size -> (T[]) Array.newInstance(elements.getClass().getComponentType(), size));
        return new EnumArray<>(filtered);
    }

    @SuppressWarnings("unchecked")
    public <R> R[] map(Function<T, R> mapper, Class<R> componentType) {
        R[] mapped = (R[]) Array.newInstance(componentType, this.elements.length);
        for (int i = 0; i < this.elements.length; i++) {
            mapped[i] = mapper.apply(this.elements[i]);
        }
        return mapped;
    }

    @Override
    public Iterator<T> iterator() {
        return Arrays.asList(elements).iterator();
    }

    public int size() {
        return this.elements.length;
    }
}