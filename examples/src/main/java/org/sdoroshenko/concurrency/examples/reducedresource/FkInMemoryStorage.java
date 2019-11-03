package org.sdoroshenko.concurrency.examples.reducedresource;

import java.util.Map;

/**
 * Fake object represents distributed in-memory storage.
 *
 * @author Sergei_Doroshenko
 * @version 1.0
 */
public class FkInMemoryStorage implements InMemoryStorage {

    private final Map<Long, User> storage;

    public FkInMemoryStorage(Map<Long, User> storage) {
        this.storage = storage;
    }

    @Override
    public User get(Long key) {
        return storage.get(key);
    }

}
