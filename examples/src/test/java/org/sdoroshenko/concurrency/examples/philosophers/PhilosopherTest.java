package org.sdoroshenko.concurrency.examples.philosophers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import static org.testng.Assert.assertEquals;

public class PhilosopherTest {

    private static final Logger log = LoggerFactory.getLogger(PhilosopherTest.class);

    @Test(invocationCount = 3)
    public void run() throws InterruptedException {

        int endExclusive = 100;
        Queue<Integer> queue = IntStream.range(1, endExclusive).boxed().collect(toLinkedList());
        int sum = endExclusive * (endExclusive - 1) / 2;

        Fork f1 = new Fork(1);
        Fork f2 = new Fork(2);
        Philosopher laoTzu = new Philosopher("1-Lao Tzu", queue, f1, f2);

        Fork f3 = new Fork(3);
        Philosopher plato = new Philosopher("2-Plato", queue, f2, f3);

        Fork f4 = new Fork(4);
        Philosopher socrates = new Philosopher("3-Socrates", queue, f3, f4);

        Fork f5 = new Fork(5);
        Philosopher nietzsche = new Philosopher("4-Nietzsche", queue, f4, f5);

        Philosopher kant = new Philosopher("5-Kant", queue, f5, f1);

        laoTzu.start();
        plato.start();
        socrates.start();
        nietzsche.start();
        kant.start();

        laoTzu.join();
        plato.join();
        socrates.join();
        nietzsche.join();
        kant.join();

        log.info("plate: {}", queue);
        log.info("{} eat: {} = {}", new Object[]{laoTzu.getName(), laoTzu.getList(), laoTzu.getSum()});
        log.info("{} eat: {} = {}", new Object[]{plato.getName(), plato.getList(), plato.getSum()});
        log.info("{} eat: {} = {}", new Object[]{socrates.getName(), socrates.getList(), socrates.getSum()});
        log.info("{} eat: {} = {}", new Object[]{nietzsche.getName(), nietzsche.getList(), nietzsche.getSum()});
        log.info("{} eat: {} = {}", new Object[]{kant.getName(), kant.getList(), kant.getSum()});

        assertEquals(laoTzu.getSum() + plato.getSum() + socrates.getSum() + nietzsche.getSum() + kant.getSum(), sum);
    }

    private <T> Collector<T, ?, Queue<T>> toLinkedList() {
        return Collector.of(LinkedList::new, Queue::add, (left, right) -> {
            left.addAll(right);
            return left;
        }, Collector.Characteristics.IDENTITY_FINISH);
    }

    private int calculateSum(List<Integer>... lists) {
        return Arrays.stream(lists).flatMap(List::stream).mapToInt(Integer::intValue).sum();
    }

    @DataProvider(name = "numbers")
    public static Object[][] numbers() {
        return new Object[][]{
                {7}, {8}, {27}, {10387}
        };
    }

    @Test(dataProvider = "numbers")
    public void sum(int endExclusive) {
        int exp = IntStream.range(1, endExclusive).sum();
        int sum = endExclusive * (endExclusive - 1) / 2;

        assertEquals(sum, exp);
    }
}