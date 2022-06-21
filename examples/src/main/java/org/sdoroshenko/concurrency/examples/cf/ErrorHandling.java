package org.sdoroshenko.concurrency.examples.cf;

import java.util.concurrent.CompletableFuture;

public class ErrorHandling {
    public static void main(String[] args) {
        CompletableFuture<Void> completion1 = new CompletableFuture<>();
        CompletableFuture<Void> completion2 = new CompletableFuture<>();

        handle(new Task("Taks 1", completion1));
        handle(new Task("Task 2", completion2));

        completion1.complete(null);
        completion2.completeExceptionally(new RuntimeException("Test"));
    }

    static class Task {
        String message;
        CompletableFuture<Void> completion;

        public Task(String message, CompletableFuture<Void> completion) {
            this.message = message;
            this.completion = completion;
        }
    }


    static void handle(Task task) {
        task.completion.whenComplete((result, exception) -> {
            if (exception == null) {
                System.out.println(task.message + " completed successfully");
            } else {
                System.out.println(task.message + " completed with error: " + exception.getMessage());
            }
        });
    }
}
