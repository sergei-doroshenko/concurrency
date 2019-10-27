package org.sdoroshenko.deadlock;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CMExecutor {

    CountDownLatch invokeAll(Collection<Runnable> tasks) {
        int n = tasks.size();
        CountDownLatch startLatch = new CountDownLatch(n);
        CountDownLatch finishLatch = new CountDownLatch(n);
        ExecutorService executor = Executors.newFixedThreadPool(n);
        tasks.forEach(
                task -> executor.submit(() -> {
                    startLatch.countDown();
                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    task.run();
                    finishLatch.countDown();
                })
        );

        return finishLatch;
    }
}
