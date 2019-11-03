package org.sdoroshenko.concurrency.examples.philosophers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Dining Philosophers Problem.
 */
public class PhilosopherSyncOrder extends Thread {
    private static final Logger log = LoggerFactory.getLogger(PhilosopherSyncOrder.class);
    private final List<Integer> list;
    private final Queue<Integer> queue;
    private final Fork left;
    private final Fork right;
    private final int thinkingTime;

    public PhilosopherSyncOrder(String name, Queue<Integer> queue, Fork left, Fork right, int thinkingTime) {
        setDaemon(true);
        setName(name);
        this.list = new ArrayList<>();
        this.queue = queue;
        this.left = left;
        this.right = right;
        this.thinkingTime = thinkingTime;
    }

    @Override
    public void run() {
        log.info("started");
        while (!queue.isEmpty() && !isInterrupted()) {
            eat(queue);
            think(thinkingTime);
        }
    }

    public void eat(Queue<Integer> queue) {
        if (left.getId() < right.getId()) {
            synchronized (left) {
                synchronized (right) {

                    Integer val = queue.poll();
                    if (val != null) {
                        // in run method in  'while (!queue.isEmpty())' queue can be not empty, but then became empty
                        log.info("eating {}", val);
                        list.add(val);
                    }
                }
            }
        } else {
            synchronized (right) {
                synchronized (left) {

                    Integer val = queue.poll();
                    if (val != null) {
                        // in run method in  'while (!queue.isEmpty())' queue can be not empty, but then became empty
                        log.info("eating {}", val);
                        list.add(val);
                    }
                }
            }
        }
    }

    public void think(long time) {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            interrupt();
        }
    }

    public List<Integer> getList() {
        return list;
    }

    public int getSum() {
        return list.stream().mapToInt(Integer::intValue).sum();
    }
}