package org.sdoroshenko.concurrency.examples.fjp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task sample.
 */
public class FibonacciTask extends RecursiveTask<Integer> {

    private static final AtomicInteger taskCounter = new AtomicInteger(0);
    private final Integer fibIndex;

    private final Map<Integer, Integer> fibMap = new HashMap<>();

    // threshold
    {
        fibMap.put(0, 0);
        fibMap.put(1, 1);
        fibMap.put(2, 1);
        fibMap.put(3, 2);
        fibMap.put(4, 3);
        fibMap.put(5, 5);
    }

    public FibonacciTask(int fibEnd) {
        this.fibIndex = fibEnd;
    }

    @Override
    protected Integer compute() {
        if (fibMap.containsKey(fibIndex)) {
            return fibMap.get(fibIndex);
        }

        FibonacciTask task1 = new FibonacciTask(fibIndex - 1);
        task1.fork();
        taskCounter.getAndIncrement();

        FibonacciTask task2 = new FibonacciTask(fibIndex - 2);
        task2.fork();
//        Integer result2 = task2.compute(); // in the same thread
        taskCounter.getAndIncrement();

        Integer result1 = task1.join();
        Integer result2 = task2.join();
        int result = result1 + result2;

        System.out.println("Intermediate result: " + result + ", thread: " + Thread.currentThread().getName());
        return result;
    }

    public static AtomicInteger getTaskCounter() {
        return taskCounter;
    }


}
