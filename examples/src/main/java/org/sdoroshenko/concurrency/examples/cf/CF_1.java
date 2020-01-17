package org.sdoroshenko.concurrency.examples.cf;

import java.util.concurrent.CompletableFuture;

public class CF_1 {
    public static void main(String[] args) throws InterruptedException {
        CompletableFuture<String> ussr = CompletableFuture.supplyAsync(() -> "USSR")
            .thenApplyAsync(s -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                s += " are Moon communists!";
                return s;
            });

        CompletableFuture<String> usa = CompletableFuture.supplyAsync(() -> "USA")
            .thenApplyAsync(s -> s += " will build first Moon base.");

        ussr.acceptEitherAsync(usa, System.out::println);

        Thread.sleep(1000);
    }
}
