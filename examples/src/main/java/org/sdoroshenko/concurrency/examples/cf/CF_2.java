package org.sdoroshenko.concurrency.examples.cf;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CF_2 {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        CompletableFuture<Void> result = CompletableFuture.supplyAsync(() -> "One", executor)
            .thenCombineAsync(CompletableFuture.supplyAsync(() -> "Two"), CF_2::combinator0)
            .thenCombineAsync(CompletableFuture.supplyAsync(() -> "The last"), CF_2::combinator0)
            .thenApply(String::toUpperCase)
            .thenAcceptAsync(System.out::println, executor);

        result.join();
        System.out.println(result.isDone());

        executor.shutdown();
    }

    private static String combinator0(String s, String u) {
        return s + ", " + u;
    }
}
