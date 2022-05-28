package org.sdoroshenko.concurrency.examples.cf_recursive;

import java.util.concurrent.atomic.AtomicInteger;

public class StackOverflow {
    private static final Log log = new Log();

    public static void main(String[] args) {
        System.out.println(method(new AtomicInteger()));
    }

    public static int method(AtomicInteger i) {
        i.incrementAndGet();
//        log.debug(k + " ");
//        log.measureStack();
        return method(i);
    }
}
