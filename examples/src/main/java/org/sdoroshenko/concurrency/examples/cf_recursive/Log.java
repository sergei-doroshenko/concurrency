package org.sdoroshenko.concurrency.examples.cf_recursive;

import java.util.concurrent.atomic.AtomicInteger;

public class Log {

    private AtomicInteger stl = new AtomicInteger();
    public void debug(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
    }

    public void measureStack() {
        int stackLength = Thread.currentThread().getStackTrace().length;
        if (stackLength > stl.get()) {
            stl.set(stackLength);
            debug("StackTrace.length=" + stackLength);
        }
    }
}
