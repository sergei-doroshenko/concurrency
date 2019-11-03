package org.sdoroshenko.concurrency.examples.reducedresource;

/**
 * Represents an external resource that fetches user by username over a network.
 */
public interface UserHttpClient extends Resource<User, String> {

}
