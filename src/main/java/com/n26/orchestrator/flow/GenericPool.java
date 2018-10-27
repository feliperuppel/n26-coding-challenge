package com.n26.orchestrator.flow;

import java.util.Set;

public interface GenericPool<T, S> {
    Set<T> getAll();
    void add(T t);
    void remove(T t);
    void clear();
    T getOldest();
    S getStatistics();
}
