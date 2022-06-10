package org.sdoroshenko.concurrency.examples.cf_recursive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Main2 {
    public static void main(String[] args) throws InterruptedException {

        Thread t = new Thread(() -> {
            Client client = new Client();

            Consumer consumer = new Consumer(client, 2, 2);

            consumer.subscribe(msg -> {
                String newMsg = String.format("%s > [%s] processed", msg, Thread.currentThread().getName());
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return CompletableFuture.completedFuture(newMsg);
            });
        });
        t.start();


        Thread.currentThread().join();
    }
}
