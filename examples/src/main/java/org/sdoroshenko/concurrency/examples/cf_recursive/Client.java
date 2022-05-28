package org.sdoroshenko.concurrency.examples.cf_recursive;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class Client {

    public CompletableFuture<String> receive(String messageNumber) {
        return CompletableFuture.supplyAsync(() -> {
//            Log.debug("Requested messages: " + messageNumber);
            return (messageNumber + " > [supplied] " + Thread.currentThread().getName());
        });
    }

    private String getMessage(int length, int chunkBound) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        IntStream.range(0, length)
                .forEach(i -> builder.append(random.nextInt(chunkBound)));

        return builder.toString();
    }
}
