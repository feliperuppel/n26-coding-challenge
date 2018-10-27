package com.n26.orchestrator.flow;

import java.util.Queue;

public interface GenericQueue<T>{
    void push(T t);
    T pop();
    Queue getQueue();
    void clear();
    boolean hasElements();
}
