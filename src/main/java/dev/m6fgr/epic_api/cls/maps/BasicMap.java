package dev.m6fgr.epic_api.cls.maps;

import java.util.Map;

public interface BasicMap<K, V> extends Map<K, V> {

    boolean removeKey(K key);

    boolean removeValue(V value);

    boolean removeIfPresent(K key, V value);

    boolean removeKeyIfPresent(K key);

    boolean removeValueIfPresent(V value);

    static <K, V> BasicMap<K, V> of() {
        return new BasicHashMap<>();
    }

    static <K, V> BasicMap<K, V> of(K k1, V v1) {
        BasicMap<K, V> map = new BasicHashMap<>();
        map.put(k1, v1);
        return map;
    }

    static <K, V> BasicMap<K, V> of(K k1, V v1, K k2, V v2) {
        BasicMap<K, V> map = new BasicHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    static <K, V> BasicMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        BasicMap<K, V> map = new BasicHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    static <K, V> BasicMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        BasicMap<K, V> map = new BasicHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    static <K, V> BasicMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        BasicMap<K, V> map = new BasicHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }


    static <K, V> BasicMap<K, V> of(Map<K, V> existingMap) {
        BasicMap<K, V> map = new BasicHashMap<>();
        if (existingMap != null) {
            map.putAll(existingMap);
        }
        return map;
    }
}