package org.sdoroshenko.concurrency.examples.cf_recursive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Consumer {
    public static final Log log = new Log();

    private final Client client;
    private final int concurrentPolls;
    private final ExecutorService executor;
    private boolean closed = false;

    public Consumer(Client client, int concurrentPolls, int processingThreadCount) {
        this.client = client;
        this.concurrentPolls = concurrentPolls;
        this.executor = new ThreadPoolExecutor(
                processingThreadCount,
                processingThreadCount,
                0,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                r -> new Thread(r, "consumer-tread-pool"),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void subscribe(Function<String, CompletableFuture<String>> processor) {
        for (int i = 0; i < concurrentPolls; i++) {
            startPollingFlow(processor, i);
        }
    }

    public void close() {
        closed = true;
        executor.shutdown();
    }

    // Starts new polling flow
    private void startPollingFlow(Function<String, CompletableFuture<String>> processor, int i) {
        if (closed) {
            return;
        }

        String initMsg = String.format("[%s] started", Thread.currentThread().getName());
        // this can be a problem
        // Consumer poll next messages batch
        // After 4 unsuccessful tries, messages go a DLQ
        // Mitigated by usage of ThreadPoolExecutor.CallerRunsPolicy()
        client.receiveAsync(initMsg).whenComplete((receiveMessageResult, exception) -> {
            int k = i + 1;
            if (exception != null) {
                log.debug(exception.getMessage());
                startPollingFlow(processor, k);
                return;
            }

            String message = String.format("%s > [%s] received", receiveMessageResult, Thread.currentThread().getName());

            executor.submit(() -> {
                processor.apply(message).whenComplete((result, processingException) -> {
                    if (processingException == null) {
                        String newResult = String.format("%s > [%s] deleted", result, Thread.currentThread().getName());
                        log.debug(newResult);
                    } else {
                        processingException.printStackTrace();
                    }
                });
            });

            startPollingFlow(processor, k);
        });
    }
}
