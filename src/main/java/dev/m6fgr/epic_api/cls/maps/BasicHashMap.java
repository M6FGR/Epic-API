package dev.m6fgr.epic_api.cls.maps;

import java.util.HashMap;

public class BasicHashMap<K, V> extends HashMap<K, V> implements BasicMap<K, V> {

    public BasicHashMap() {
        super();
    }

    public BasicHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public boolean removeKey(K key) {
        return this.remove(key) != null;
    }

    public boolean removeValue(V value) {
        return this.values().remove(value);
    }

    public boolean removeIfPresent(K key, V value) {
        if (this.containsKey(key) && this.containsValue(value)) {
            return super.remove(key, value);
        }
        return false;
    }

    public boolean removeValueIfPresent(V value) {
        if (this.containsValue(value)) {
            return this.removeValue(value);
        }
        return false;
    }

    public boolean removeKeyIfPresent(K key) {
        if (this.containsKey(key)) {
            return this.removeKey(key);
        }
        return false;
    }
}