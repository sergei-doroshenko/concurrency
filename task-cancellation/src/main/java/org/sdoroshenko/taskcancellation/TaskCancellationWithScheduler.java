package org.sdoroshenko.taskcancellation;


import org.sdoroshenko.utils.Await;

import java.util.concurrent.*;

/**
 * Cancelling task by scheduling call of {@link Future#cancel(boolean)}.
 */
public class TaskCancellationWithScheduler {

    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static class TestTask implements Runnable {
        private int counter;

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                if (Thread.currentThread().isInterrupted())
                    break;

                counter++;
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
        scheduler.schedule(() -> future.cancel(true), 100, TimeUnit.MILLISECONDS);

        try {
            // block on get use isDone for non blocking
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Task execution exception");
        }

        TimeUnit.SECONDS.sleep(3);

        System.out.println(task.getCounter());

        System.exit(0);
    }

}
