package org.sdoroshenko.concurrency.examples.philosophers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import static org.testng.Assert.*;

public class PhilosopherSyncOrderTest {

    private static final Logger log = LoggerFactory.getLogger(PhilosopherSyncOrderTest.class);

    @Test(timeOut = 30 * 1000)
    public void testRun() throws InterruptedException {
        int endExclusive = 100;
        Collector<Integer, ?, Queue<Integer>> collector = Collector.of(
                LinkedList::new,
                Queue::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                Collector.Characteristics.IDENTITY_FINISH
        );

        Queue<Integer> queue = IntStream.range(1, endExclusive).boxed().collect(collector);

        Fork f1 = new Fork(1);
        Fork f2 = new Fork(2);
        PhilosopherSyncOrder laoTzu = new PhilosopherSyncOrder("1-Lao Tzu", queue, f1, f2);

        Fork f3 = new Fork(3);
        PhilosopherSyncOrder plato = new PhilosopherSyncOrder("2-Plato", queue, f2, f3);

        Fork f4 = new Fork(4);
        PhilosopherSyncOrder socrates = new PhilosopherSyncOrder("3-Socrates", queue, f3, f4);

        Fork f5 = new Fork(5);
        PhilosopherSyncOrder nietzsche = new PhilosopherSyncOrder("4-Nietzsche", queue, f4, f5);

        PhilosopherSyncOrder kant = new PhilosopherSyncOrder("5-Kant", queue, f5, f1);

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

        assertTrue(true);
    }
}