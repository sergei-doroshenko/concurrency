package org.sdoroshenko.concurrency.examples.reducedresource;

/**
 * Represents an external resource.
 * @param <T> a key
 * @param <K> a value
 */
public interface Resource<T, K> {

    T get(K key);

}
