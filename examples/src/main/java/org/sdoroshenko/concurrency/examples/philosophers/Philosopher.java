package org.sdoroshenko.concurrency.examples.philosophers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * Dining Philosophers Problem.
 */
public class Philosopher extends Thread {
    private static final Logger log = LoggerFactory.getLogger(Philosopher.class);
    private final List<Integer> list;
    private final Queue<Integer> queue;
    private final Fork left;
    private final Fork right;

    public Philosopher(String name, Queue<Integer> queue, Fork left, Fork right) {
        setDaemon(true);
        setName(name);
        this.list = new ArrayList<>();
        this.queue = queue;
        this.left = left;
        this.right = right;
    }

    @Override
    public void run() {
        log.info("started");
        while (!queue.isEmpty()) {
            eat();
            think(1000);
        }
    }

    public void eat() {
        if (left.getId() < right.getId()) {
            synchronized (left) {
                while (!left.isFree()) {
                    try {
                        left.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                left.setFree(false);

                synchronized (right) {
                    while (!right.isFree()) {
                        try {
                            right.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    right.setFree(false);

                    Integer val;
                    synchronized (queue) {
                        val = queue.poll();
                    }
                    if (val != null) {
                        // in run method in  'while (!queue.isEmpty())' queue can be not empty, but then became empty
                        log.info("eating {}", val);
                        list.add(val);
                    }

                    right.setFree(true);
                    right.notifyAll();
                }

                left.setFree(true);
                left.notifyAll();
            }
        } else {
            synchronized (right) {
                while (!right.isFree()) {
                    try {
                        right.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                right.setFree(false);

                synchronized (left) {
                    while (!left.isFree()) {
                        try {
                            left.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    left.setFree(false);

                    Integer val;
                    synchronized (queue) {
                        val = queue.poll();
                    }
                    if (val != null) {
                        // in run method in  'while (!queue.isEmpty())' queue can be not empty, but then became empty
                        log.info("eating {}", val);
                        list.add(val);
                    }

                    left.setFree(true);
                    left.notifyAll();
                }

                right.setFree(true);
                right.notifyAll();
            }
        }
    }

    public void think(long time) {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public List<Integer> getList() {
        return list;
    }

    public int getSum() {
        return list.stream().mapToInt(Integer::intValue).sum();
    }
}