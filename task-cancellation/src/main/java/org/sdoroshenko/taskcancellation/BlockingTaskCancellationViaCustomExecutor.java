package org.sdoroshenko.taskcancellation;

import org.sdoroshenko.websocket.DateSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Blocking Socket cancellation via custom executor.
 */
public class BlockingTaskCancellationViaCustomExecutor {

    private static final Logger mainLogger = LoggerFactory.getLogger(BlockingTaskCancellationViaCustomExecutor.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 4455;
        DateSocketServer server = new DateSocketServer();
        server.start(port, 700);

        ThreadPoolExecutor executor = new CancellingExecutor(2);
        // Connecting a client task
        SocketUsingTask<String> task = new SocketUsingTask<String>() {
            @Override
            public String call() throws Exception {
                try {
                    PrintWriter out = new PrintWriter(super.socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(super.socket.getInputStream()));

                    mainLogger.debug("Connected to server...");
                    out.println("Hello server");

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        mainLogger.debug("Client get: " + inputLine);
                        if (".".equals(inputLine)) {
                            out.println("good bye");
                            break;
                        }
                        out.println("Timestamp: " + System.currentTimeMillis());
                    }
                } catch (IOException e) {
                    mainLogger.error("Client got exception: " + e);
                }
                return null;
            }
        };
        task.setSocket(new Socket("127.0.0.1", port));
        executor.submit(task);

        ScheduledExecutorService timeoutExecutor = Executors.newSingleThreadScheduledExecutor();
        timeoutExecutor.schedule(() -> task.cancel(), 5, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(10);
        server.stop();
        System.exit(0);
    }

    /**
     * Custom executor.
     */
    public static class CancellingExecutor extends ThreadPoolExecutor {

        public CancellingExecutor(int corePoolSize) {
            super(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        }

        /**
         * Crucial logic is here.
         * @param callable a callable
         * @return task
         */
        @Override
        protected RunnableFuture newTaskFor(Callable callable) {
            if (callable instanceof CancellableTask) { // here we determine a callable
                return ((CancellableTask) callable).newTask();
            } else {
                return super.newTaskFor(callable); // A regular Callable, delegate to parent
            }
        }
    }

    public interface CancellableTask<T> extends Callable<T> {
        void cancel(); // Method for supporting non-standard cancellation
        RunnableFuture<T> newTask();
    }

    public static abstract class SocketUsingTask<T> implements CancellableTask<T> {

        private static final Logger taskLogger = LoggerFactory.getLogger(SocketUsingTask.class);
        private Socket socket;

        public synchronized void setSocket(Socket socket) {
            this.socket = socket;
        }

        @Override
        public RunnableFuture<T> newTask() {
            return new FutureTask<T>(this) {
                public boolean cancel(boolean mayInterruptIfRunning) {
                    try {
                        SocketUsingTask.this.cancel();
                    } finally {
                        return super.cancel(mayInterruptIfRunning);
                    }
                }
            };
        }

        @Override
        public void cancel() {
            taskLogger.debug("Task cancellation");
            try {
                if (socket != null) {
                    taskLogger.debug("Closing socket: " + socket);
                    /*
                    Closing the underlying socket makes any thread blocked in read or write
                    throw a SocketException
                     */
                    socket.close();
                }
            } catch (IOException e) {
                taskLogger.error("Client got exception: " + e);
            }
        }
    }
}
