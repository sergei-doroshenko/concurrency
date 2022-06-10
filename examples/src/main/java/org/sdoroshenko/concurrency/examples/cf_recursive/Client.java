package org.sdoroshenko.concurrency.examples.cf_recursive;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class Client {

    public static final ForkJoinPool pool = new ForkJoinPool(1);

    public CompletableFuture<String> receive(String messageNumber) {
        return CompletableFuture.completedFuture(String.format("%s > [%s] client", messageNumber, Thread.currentThread().getName()));
    }

    public CompletableFuture<String> receiveAsync(String messageNumber) {
        return CompletableFuture.supplyAsync(() -> String.format("%s > [%s] client", messageNumber, Thread.currentThread().getName()), pool);
    }

    private String getMessage(int length, int chunkBound) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        IntStream.range(0, length)
                .forEach(i -> builder.append(random.nextInt(chunkBound)));

        return builder.toString();
    }
}
