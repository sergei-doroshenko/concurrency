package org.sdoroshenko.concurrency.examples.parallelstream;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class ParallelStream {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        getSumOfDoubled();

        ForkJoinPool pool = new ForkJoinPool(2);
        CompletableFuture.runAsync(() -> getSumOfDoubled(), pool).get();
    }

    private static int getSumOfDoubled() {
        return IntStream.range(0, 10)
                .parallel()
                .map(i -> {
                    System.out.println(Thread.currentThread() + " " + i);
                    return i * 2;
                })
                .sum();
    }
}
