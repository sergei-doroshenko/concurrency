package org.sdoroshenko.concurrency.examples.reducedresource;

import java.util.concurrent.atomic.AtomicInteger;

public class CallCountable<T, K> implements Resource<T, K> {
    private final AtomicInteger callCounter = new AtomicInteger();
    private final Resource<T,K> resource;

    public CallCountable(Resource<T, K> resource) {
        this.resource = resource;
    }

    @Override
    public T get(K key) {
        callCounter.getAndIncrement();
        return resource.get(key);
    }

    public int getCallCount() {
        return callCounter.get();
    }
}
