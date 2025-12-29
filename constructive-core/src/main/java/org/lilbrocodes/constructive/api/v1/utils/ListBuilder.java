package org.lilbrocodes.constructive.api.v1.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListBuilder<B, V> {
    private final List<V> emptyDefaults;
    private List<V> list = new ArrayList<>();
    private final B parent;

    private ListBuilder(B parent, List<V> emptyDefaults) {
        this.emptyDefaults = emptyDefaults;
        this.parent = parent;
    }

    public static <B, V> ListBuilder<B, V> of(B parent) {
       return of(parent, new ArrayList<>());
    }

    public static <B, V> ListBuilder<B, V> of(B parent, List<V> emptyDefaults) {
        return new ListBuilder<>(parent, emptyDefaults);
    }

    public ListBuilder<B, V> push(V elem) {
        list.add(elem);
        return this;
    }

    @SafeVarargs
    public final ListBuilder<B, V> push(V... elements) {
        list.addAll(Arrays.stream(elements).toList());
        return this;
    }

    public ListBuilder<B, V> pop() {
        if (!list.isEmpty()) list.remove(list.size() - 1);
        return this;
    }

    public ListBuilder<B, V> clear() {
        list.clear();
        return this;
    }

    public B end() {
        return parent;
    }

    public List<V> build() {
        return new ArrayList<>(list.isEmpty() ? emptyDefaults : list);
    }

    public V[] toArray(Class<V> clazz) {
        List<V> built = build();
        @SuppressWarnings("unchecked")
        V[] arr = (V[]) Array.newInstance(clazz, built.size());
        return built.toArray(arr);
    }

    public ListBuilder<B, V> set(List<V> list) {
        this.list = list;
        return this;
    }
}
