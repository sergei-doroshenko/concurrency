package org.sdoroshenko.shutdown;

import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Producer extends Thread {
    private final Queue<Integer> queue;
    private final int timeout;
    private int counter;

    public Producer(Queue<Integer> queue, int timeout) {
        this.queue = queue;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        System.out.println("Producer started");
        boolean run = true;
        while (run) {
            counter++;

            try {
                TimeUnit.SECONDS.sleep(timeout);
            } catch (InterruptedException e) {
                System.out.println("Producer interrupted");
                break;
            }

            synchronized (queue) {
                while (!queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Consumer interrupted");
                        run = false;
                        break;
                    }
                }
                queue.add(ThreadLocalRandom.current().nextInt(1000) + 10);
                queue.notify();
            }

            System.out.print("=");
        }
        System.out.printf("Producer stopped. Operations: %d\n", counter);
    }
}
