package org.sdoroshenko.taskcancellation;

import org.sdoroshenko.websocket.DateSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Cancelling blocking task via reference.
 */
public class BlockingTaskCancellationViaFutureMap {

    public static void main(String[] args) throws IOException, InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(2);
        ScheduledExecutorService timeoutExecutor = Executors.newSingleThreadScheduledExecutor();

        int port = 4455;
        DateSocketServer server = new DateSocketServer();
        server.start(port, 700);

        // This is the main logic - the map
        Map<Future<?>, CancellableRunnable> taskMap = new HashMap<>();
        CancellableRunnable task = new SocketRunnable("127.0.0.1", port);
        Future<?> future = executor.submit(task);
        taskMap.put(future, task); // <-- important

        timeoutExecutor.schedule(() -> {
            future.cancel(true); // cancel Future
            taskMap.get(future).cancel(); // cancel task
        }, 5, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(20);
        server.stop();
        System.exit(0);
    }

    public interface CancellableRunnable extends Runnable {
        void cancel();
    }

    public static class SocketRunnable implements CancellableRunnable {

        private static final Logger socketRunnableLog = LoggerFactory.getLogger(SocketRunnable.class);

        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public SocketRunnable(String ip, int port) throws IOException {
            this.socket = new Socket(ip, port);
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                socketRunnableLog.debug("Connecting to server...");
                out.println("Hello server");

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    socketRunnableLog.debug("Client get: " + inputLine);
                    if (".".equals(inputLine)) {
                        out.println("good bye");
                        break;
                    }
                    out.println("Timestamp: " + System.currentTimeMillis());
                }
            } catch (IOException e) {
                socketRunnableLog.error("Client got exception: " + e);
            }
        }

        @Override
        public void cancel() {
            socketRunnableLog.debug("Client canceled...");
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                socketRunnableLog.error("Client got exception: " + e);
            }
        }
    }
}
