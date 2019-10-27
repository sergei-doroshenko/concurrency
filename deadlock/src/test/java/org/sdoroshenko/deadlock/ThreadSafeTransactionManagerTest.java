package org.sdoroshenko.deadlock;

import org.junit.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class ThreadSafeTransactionManagerTest {

    Account dollars;
    Account euros;
    Map<Integer, Account> accounts;
    ThreadSafeTransactionManager txManager;

    @Before
    public void setUp() throws Exception {
        dollars = new Account(1, "USD");
        dollars.add(1000);
        euros = new Account(2, "EUR");
        euros.add(500);

        accounts = new HashMap<>();
        accounts.put(1, dollars);
        accounts.put(2, euros);
        txManager = new ThreadSafeTransactionManager(100);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeTransactionThrowsExceptionIfSumLessFrom() {
        txManager.exec(euros, dollars, 100000);
    }

    @Test
    public void executeTransaction() throws InterruptedException {
        int threads = 4;
        CountDownLatch startLatch = new CountDownLatch(threads);
        CountDownLatch finishLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        // transactions number should be equal to threads
        executor.submit(getRunnable(startLatch, finishLatch, dollars, euros, 200)); // USD: 1000-200=800  EUR: 500+200=700
        executor.submit(getRunnable(startLatch, finishLatch, euros, dollars, 200)); // USD: 800 +200=1000 EUR: 700-200=500
        executor.submit(getRunnable(startLatch, finishLatch, euros, dollars, 300)); // USD: 1000+300=1300 EUR: 500-300=200
        executor.submit(getRunnable(startLatch, finishLatch, dollars, euros, 400)); // USD: 1300-400=900  EUR: 200+400=600

        finishLatch.await();
        assertEquals(1500, dollars.getAmount().longValue() + euros.getAmount().longValue());
        assertEquals(900, dollars.getAmount().longValue());
        assertEquals(600, euros.getAmount().longValue());
    }

    /**
     * Tests transaction execution WITH appropriate synchronization in a loop.
     * Should always pass.
     * @throws InterruptedException in {@link CountDownLatch#await()}
     */
    @Test
    public void executeTransactionLoop() throws InterruptedException {
        new CMExecutor().invokeAll(generateTasks(20)).await();
        assertEquals(1500, dollars.getAmount().longValue() + euros.getAmount().longValue());
    }

    private Collection<Runnable> generateTasks(int n) {
        return IntStream.range(0, n).mapToObj(i -> generateTask()).collect(Collectors.toList());
    }

    private Runnable generateTask() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int id = random.nextInt(10) % 2 == 0 ? 1 : 2;
        Account from = accounts.get(id);
        Account to = accounts.get(id == 1 ? 2 : 1);
        long sum = random.nextLong(from.getAmount().longValue() / 2);

        return () -> txManager.exec(from, to, sum);
    }

    private Runnable getRunnable(CountDownLatch sl, CountDownLatch fl, Account from, Account to, long sum) {
        return () -> {
            sl.countDown();
            try {
                sl.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            txManager.exec(from, to, sum);
            fl.countDown();
        };
    }
}