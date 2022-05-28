package org.sdoroshenko.concurrency.examples.cf_recursive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Main2 {
    public static void main(String[] args) throws InterruptedException {

        Client client = new Client();

        Consumer consumer = new Consumer(client, 2, 2);

        consumer.subscribe(msg -> {
            String newMsg = msg +  " > [processed] " + Thread.currentThread().getName();
//            Consumer.log(newMsg);
            try {
                TimeUnit.SECONDS.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return CompletableFuture.completedFuture(newMsg);
        });

        Thread.currentThread().join();
    }
}
