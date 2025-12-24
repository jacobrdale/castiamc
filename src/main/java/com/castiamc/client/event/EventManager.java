package com.castiamc.client.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private final Map<Class<?>, List<Object>> listeners = new HashMap<>();
    private final Object client;

    public EventManager(Object client) {
        this.client = client;
    }

    public <T> void add(Class<T> cls, T listener) {
        listeners.computeIfAbsent(cls, k -> new ArrayList<>()).add(listener);
    }

    public <T> void remove(Class<T> cls, T listener) {
        if (listeners.containsKey(cls)) listeners.get(cls).remove(listener);
    }

    public <T> List<T> getListeners(Class<T> cls) {
        return (List<T>) listeners.getOrDefault(cls, new ArrayList<>());
    }
}
