package org.sdoroshenko.concurrency.examples.reducedresource;

import java.util.concurrent.TimeUnit;

/**
 * Implements an http user network.
 */
public class FkUserHttpClient implements UserHttpClient {

    @Override
    public User get(String username) {

        try {
            TimeUnit.SECONDS.sleep(5L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new User(1, "http", "Peter Parker");
    }

}
