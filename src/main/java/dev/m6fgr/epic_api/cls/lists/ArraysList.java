package dev.m6fgr.epic_api.cls.lists;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ArraysList<T> extends ArrayList<T> {

    protected static final int DEFAULT_CAPACITY = 64;

    public ArraysList() {
        super(DEFAULT_CAPACITY);
    }

    public ArraysList(int initialCapacity) {
        super(initialCapacity);
    }

    public ArraysList(Collection<? extends T> c) {
        super(c);
    }

    @SafeVarargs
    public final boolean addAsArray(T... all) {
        if (all == null || all.length == 0) return false;
        return this.addAll(Arrays.asList(all));
    }

    @SafeVarargs
    public final boolean removeAsArray(T... all) {
        if (all == null || all.length == 0) return false;
        return this.removeAll(Arrays.asList(all));
    }

    @SafeVarargs
    public final boolean containsAllArray(T... all) {
        if (all == null || all.length == 0) return true;
        return this.containsAll(Arrays.asList(all));
    }

    @SafeVarargs
    public final boolean containsAnyArray(T... all) {
        if (all == null) return false;
        for (T item : all) {
            if (this.contains(item)) {
                return true;
            }
        }
        return false;
    }

    public int cut(int at) {
        if (at >= 0 && this.size() > at) {
            this.subList(at, this.size()).clear();
        }
        return this.size();
    }

    @SuppressWarnings("unchecked")
    public T[] getArray(Class<T> clazz) {
        T[] array = (T[]) Array.newInstance(clazz, this.size());
        return this.toArray(array);
    }

    public Object[] getObjectArray() {
        return this.toArray();
    }
}