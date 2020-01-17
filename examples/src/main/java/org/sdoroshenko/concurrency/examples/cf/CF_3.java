package org.sdoroshenko.concurrency.examples.cf;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CF_3 {
    public static void main(String[] args) {
        CompletableFuture<String> f0 = CompletableFuture.supplyAsync(() -> "Soyuz 19");
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Apollo 18");
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "UFO");

        String result = Stream.of(f0, f1, f2).map(CompletableFuture::join).collect(Collectors.joining(" "));
        System.out.println(result);
    }
}
