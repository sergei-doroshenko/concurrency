package org.sdoroshenko.shutdown;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class Consumer extends Thread {
    private final Queue<Integer> queue;
    private final int timeout;
    private int counter;
    private int sum;

    public Consumer(Queue<Integer> queue, int timeout) {
        this.queue = queue;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        System.out.println("Consumer started");
        boolean run = true;
        while (run) {
            counter++;

            try {
                TimeUnit.SECONDS.sleep(timeout);
            } catch (InterruptedException e) {
                System.out.println("Consumer interrupted");
                break;
            }

            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Consumer interrupted");
                        run = false;
                        break;
                    }
                }
                sum += queue.poll();
                queue.notify();
            }

            System.out.print("=");
        }
        System.out.printf("Consumer stopped. Operations: %d, sum: %d, avg: %d\n", counter, sum, sum / counter);
    }

    private void printProgress(int counter) {
        int size = 20;
        for (int i = 0; i < counter; i++) {
            System.out.print("=");
        }
        System.out.print(">");
        for (int i = 0; i < size - counter; i++) {
            System.out.print(" ");
        }
        System.out.print("|\r");
    }
}
