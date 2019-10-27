package org.sdoroshenko.taskcancellation;


import org.sdoroshenko.concurrency.utils.Await;
import org.sdoroshenko.concurrency.utils.LaunderThrowable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Cancelling task via {@link Future}.
 */
public class TaskCancellationViaFuture {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static class TestTask implements Runnable {
        private int counter;

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                if (Thread.currentThread().isInterrupted())
                    break;

                counter++; // 
                Await.await1(110);
            }

        }

        public int getCounter() {
            return counter;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        TestTask task = new TestTask();

        Future<?> future = executor.submit(task);

        try {
            // block on get use isDone for non blocking
            future.get(100, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            System.out.println("Task execution time exceed");
            System.out.println(e);
            // future will be cancelled below
        } catch (ExecutionException e) {
            System.out.println("Task execution exception");

            // exception thrown in future; rethrow
            throw LaunderThrowable.launderThrowable(e.getCause());
        } finally {
            System.out.println("Task cancelling...");
            // Harmless if future already completed
            future.cancel(true); // interrupt if running
        }

        TimeUnit.SECONDS.sleep(10);

        System.out.println(task.getCounter());

        System.exit(0);
    }

}
