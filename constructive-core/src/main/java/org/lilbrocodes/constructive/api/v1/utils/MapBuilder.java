package org.lilbrocodes.constructive.api.v1.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MapBuilder<B, K, V> {
    private final Map<K, V> emptyDefaults;
    private final Map<K, V> map = new HashMap<>();
    private final B parent;

    private MapBuilder(B parent, Map<K, V> emptyDefaults) {
        this.emptyDefaults = emptyDefaults;
        this.parent = parent;
    }

    public static <B, K, V> MapBuilder<B, K, V> of(B parent, Map<K, V> emptyDefaults) {
        return new MapBuilder<>(parent, emptyDefaults);
    }

    public static <B, K, V> MapBuilder<B, K, V> of(B parent) {
        return of(parent, new HashMap<>());
    }

    public ValueBuilder<MapBuilder<B, K, V>, K, V> key(K key) {
        return new ValueBuilder<>(this, key, map::put);
    }

    public MapBuilder<B, K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public MapBuilder<B, K, V> clear() {
        map.clear();
        return this;
    }

    public B end() {
        return parent;
    }

    public Map<K, V> build() {
        return new HashMap<>(map.isEmpty() ? emptyDefaults : map);
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static class ValueBuilder<B, K, V> {
        private final B parent;
        private final K key;
        private final BiConsumer<K, V> add;

        public ValueBuilder(B parent, K key, BiConsumer<K, V> add) {
            this.parent = parent;
            this.key = key;
            this.add = add;
        }

        public B value(V value) {
            this.add.accept(key, value);
            return parent;
        }
    }
}
