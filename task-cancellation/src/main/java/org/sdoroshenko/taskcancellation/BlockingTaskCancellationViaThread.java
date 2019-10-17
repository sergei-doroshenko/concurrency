package org.sdoroshenko.taskcancellation;

import org.sdoroshenko.websocket.DateSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Cancelling blocking task via custom thread.
 */
public class BlockingTaskCancellationViaThread {

    public static void main(String[] args) throws InterruptedException {
        int port = 4455;
        DateSocketServer server = new DateSocketServer();
        server.start(port, 700);

        try {
            ReaderThread readerThread = new ReaderThread(new Socket("127.0.0.1", port));
            readerThread.start();
            TimeUnit.SECONDS.sleep(10);
            readerThread.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TimeUnit.SECONDS.sleep(5);
        server.stop();
        System.exit(0);
    }
}

/**
 * ReaderThread.
 * <p/>
 * Encapsulating nonstandard cancellation in a Thread by overriding interrupt.
 *
 * @author Brian Goetz and Tim Peierls
 */
class ReaderThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ReaderThread.class);
    private static final int BUFSZ = 1024;
    private final Socket socket;
    private final InputStream in;
    private final PrintWriter out;

    public ReaderThread(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void interrupt() {
        try {
            logger.debug("Client canceled...");
            socket.close();
            in.close();
        } catch (IOException e) {
            logger.error("Client got exception: " + e);
        } finally {
            super.interrupt();
        }
    }

    @Override
    public void run() {

        try {
            logger.debug("Connected to server...");
            out.println("Hello server");

            byte[] buf = new byte[BUFSZ];
            while (true) {
                int count = in.read(buf);
                if (count < 0) {
                    break;
                } else if (count > 0) {
                    processBuffer(buf, count);
                }
                out.println("Timestamp: " + System.currentTimeMillis());
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            /* Allow thread to exit */
        }

        super.run();
    }

    private void processBuffer(byte[] buf, int count) {
        String message = new String(buf, 0, count, StandardCharsets.UTF_8);
        logger.debug("Client get: " + message);
    }

}
