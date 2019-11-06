package org.sdoroshenko.concurrency.examples.fjp;

import org.testng.annotations.Test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

public class ForkJoinTest {

    @Test
    public void fibonacci() {
        int fibEnd = 20;
        ForkJoinPool pool = new ForkJoinPool();

        FibonacciTask task = new FibonacciTask(fibEnd);
        Integer fibNum = pool.invoke(task);
        System.out.println("FibNumber: " + fibNum + " for: " + fibEnd);
        System.out.println("Task count: " + FibonacciTask.getTaskCounter());
    }

    static class FibonacciTask extends RecursiveTask<Integer> {
        private static final AtomicInteger taskCounter = new AtomicInteger(0);
        private final Integer fibEnd;

        public FibonacciTask(int fibEnd) {
            this.fibEnd = fibEnd;
        }

        @Override
        protected Integer compute() {
            if (fibEnd == 0) {
                return 0;
            } else if (fibEnd == 1) {
                return 1;
            } else {
                FibonacciTask task1 = new FibonacciTask(fibEnd - 1);
                task1.fork();
                taskCounter.getAndIncrement();
                Integer result1 = task1.join();

                FibonacciTask task2 = new FibonacciTask(fibEnd - 2);
                task2.fork();
                taskCounter.getAndIncrement();
                Integer result2 = task2.join();

                return result1 + result2;
            }
        }

        public static AtomicInteger getTaskCounter() {
            return taskCounter;
        }
    }
}
