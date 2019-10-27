package org.sdoroshenko.deadlock;

import org.junit.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.junit.Assert.assertNotEquals;

public class NotThreadSafeTransactionManagerTest {

    Account dollars;
    Account euros;
    Map<Integer, Account> accounts;
    NotThreadSafeTransactionManager txManager;

    @Before
    public void setUp() throws Exception {
        dollars = new Account(1, "USD");
        dollars.add(1000);
        euros = new Account(2, "EUR");
        euros.add(500);

        accounts = new HashMap<>();
        accounts.put(1, dollars);
        accounts.put(2, euros);
        txManager = new NotThreadSafeTransactionManager(100);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeTransactionThrowsExceptionIfSumLessFrom() {
        txManager.exec(euros, dollars, 100000);
    }

    /**
     * Tests transaction execution without synchronization. Transaction created manually.
     * In half the cases falls.
     * @throws InterruptedException in {@link CountDownLatch#await()}
     */
    @Ignore
    @Test
    public void executeTransaction() throws InterruptedException {
        int threads = 3;
        CountDownLatch startLatch = new CountDownLatch(threads);
        CountDownLatch finishLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        executor.submit(getRunnable(startLatch, finishLatch, dollars, euros, 200)); // USD=800 EUR=700
        executor.submit(getRunnable(startLatch, finishLatch, euros, dollars, 200)); // USD=1000 EUR=500
        executor.submit(getRunnable(startLatch, finishLatch, euros, dollars, 300)); // USD=1300 EUR=200
        executor.submit(getRunnable(startLatch, finishLatch, dollars, euros, 400)); // USD=900 EUR=600

        finishLatch.await();
        assertNotEquals(1500, dollars.getAmount().longValue() + euros.getAmount().longValue());
        assertNotEquals(900, dollars.getAmount().longValue());
        assertNotEquals(600, euros.getAmount().longValue());
    }

    /**
     * Tests transaction execution without synchronization in a loop.
     * In half the cases falls.
     * @throws InterruptedException in {@link CountDownLatch#await()}
     */
    @Ignore
    @Test
    public void executeTransaction2() throws InterruptedException {
        int threads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(threads);
        CountDownLatch finishLatch = new CountDownLatch(threads);
        IntStream.range(0, threads).forEach(
                i -> executor.submit(getRunnable(startLatch, finishLatch, accounts))
        );

        finishLatch.await();
        assertNotEquals(1500, dollars.getAmount().longValue() + euros.getAmount().longValue());
    }

    @Ignore
    @Test
    public void executeTransactionLoop() throws InterruptedException {
        int threads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(threads);
        CountDownLatch finishLatch = new CountDownLatch(threads);
        IntStream.range(0, threads).forEach(
                i -> executor.submit(getSyncRunnable(startLatch, finishLatch, accounts))
        );

        finishLatch.await();
        assertNotEquals(1500, dollars.getAmount().longValue() + euros.getAmount().longValue());
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

    private Runnable getRunnable(CountDownLatch sl, CountDownLatch fl, Map<Integer, Account> accounts) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int id = random.nextInt(10) % 2 == 0 ? 1 : 2;
        Account from = accounts.get(id);
        Account to = accounts.get(id == 1 ? 2 : 1);
        long sum = random.nextLong(from.getAmount().longValue() / 2);

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

    private Runnable getSyncRunnable(CountDownLatch sl, CountDownLatch fl, Map<Integer, Account> accounts) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int id = random.nextInt(10) % 2 == 0 ? 1 : 2;
        Account from = accounts.get(id);
        Account to = accounts.get(id == 1 ? 2 : 1);
        long sum = random.nextLong(from.getAmount().longValue() / 2);

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