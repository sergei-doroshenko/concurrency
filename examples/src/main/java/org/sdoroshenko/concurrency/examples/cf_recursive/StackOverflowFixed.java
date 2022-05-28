package org.sdoroshenko.concurrency.examples.cf_recursive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class StackOverflowFixed {
    private static final Log log = new Log();

    public static void main(String[] args) throws InterruptedException {
        method(new AtomicInteger(10), 100831);
        Thread.currentThread().join();
    }

    public static CompletableFuture<AtomicInteger> method(AtomicInteger i, int target) {
        if (i.get() == target) {
            log.debug("Result: " + i);
            return CompletableFuture.completedFuture(i);
        }

        // i.incrementAndGet();
        // java.lang.StackOverflowError
//        return method(i, target);

        return increment(i).whenComplete((result, throwable) -> {
//            log.measureStack();
//            log.debug(result + "");
            method(result, target);
        });
    }

    private static CompletableFuture<AtomicInteger> increment(AtomicInteger i) {
        i.incrementAndGet();

        // Executes in main thread and causes a deadlock after some time,
        // because of Thread.currentThread().join();
        // return CompletableFuture.completedFuture(1);

        return CompletableFuture.supplyAsync(() -> i);
    }
}
