package com.jereprograma.myttrpg.core.events;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventBus {
    private static final Map<Class<?>, List<Consumer<?>>> listeners = new ConcurrentHashMap<>();

    public static <T> void register(Class<T> type, Consumer<T> handler) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(handler);
    }

    @SuppressWarnings("unchecked")
    public static <T> void fire(T event) {
        var lst = listeners.get(event.getClass());
        if (lst != null) {
            for (var h : lst) {
                ((Consumer<T>) h).accept(event);
            }
        }
    }
}
