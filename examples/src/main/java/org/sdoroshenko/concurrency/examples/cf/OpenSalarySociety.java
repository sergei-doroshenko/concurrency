package org.sdoroshenko.concurrency.examples.cf;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OpenSalarySociety {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        int result = CompletableFuture.supplyAsync(() -> "Hello").thenApply(str -> str.length())
            .thenCombine(
                CompletableFuture.supplyAsync(() -> " World").thenApply(str -> str.length()), (s1, s2) -> s1 + s2
            ).join();

        // thenCombine
        System.out.println(result);
    }
}
