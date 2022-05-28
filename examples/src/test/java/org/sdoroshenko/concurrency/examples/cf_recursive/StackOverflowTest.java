package org.sdoroshenko.concurrency.examples.cf_recursive;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.*;

public class StackOverflowTest {

    private static final Log log = new Log();

    @BeforeMethod
    public void setUp() {
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void testMethod() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();

        try {
            StackOverflow.method(counter);
        } catch (Throwable t) {
            log.debug(t.getClass().getName());
        }

        log.debug("Counter: " + counter.get());

        AtomicInteger newCounter = new AtomicInteger();

        final int target = counter.get() * 100;
        StackOverflowFixed.method(newCounter, target);

        TimeUnit.SECONDS.sleep(2);

        log.debug("New counter: " + newCounter.get());

        assertEquals(newCounter.get(), target);
    }
}