package org.sdoroshenko.concurrency.examples.philosophers;

import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import static org.testng.Assert.assertTrue;

public class PhilosopherTest {

    @Test
    public void run() throws InterruptedException {

        Queue<Integer> queue = IntStream.range(1, 100).boxed().collect(toLinkedList());

        Fork f1 = new Fork(1);
        Fork f2 = new Fork(2);
        Philosopher ph1 = new Philosopher("Ph-1", queue, f1, f2);

        Fork f3 = new Fork(3);
        Philosopher ph2 = new Philosopher("Ph-2", queue, f2, f3);

        Fork f4 = new Fork(4);
        Philosopher ph3 = new Philosopher("Ph-3", queue, f3, f4);

        Fork f5 = new Fork(5);
        Philosopher ph4 = new Philosopher("Ph-4", queue, f4, f5);

        Philosopher ph5 = new Philosopher("Ph-5", queue, f5, f1);

        ph1.start();
        ph2.start();
        ph3.start();
        ph4.start();
        ph5.start();

        ph1.join();
        ph2.join();
        ph3.join();
        ph4.join();
        ph5.join();

        System.out.println("plate: " + queue);

        System.out.println(ph1.getList());
        System.out.println(ph2.getList());
        System.out.println(ph3.getList());
        System.out.println(ph4.getList());
        System.out.println(ph5.getList());

        assertTrue(true);
    }

    private <T> Collector<T, ?, Queue<T>> toLinkedList() {
        return Collector.of(LinkedList::new, Queue::add, (left, right) -> {
            left.addAll(right);
            return left;
        }, Collector.Characteristics.IDENTITY_FINISH);
    }
}