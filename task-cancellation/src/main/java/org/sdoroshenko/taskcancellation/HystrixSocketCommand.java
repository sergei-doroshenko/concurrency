package org.sdoroshenko.taskcancellation;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.sdoroshenko.websocket.DateSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Command with timeout.
 */
public class HystrixSocketCommand extends HystrixCommand<String> {

    private static final Logger logger = LoggerFactory.getLogger(HystrixSocketCommand.class);
    private final Socket socket;

    public HystrixSocketCommand(Socket socket) {
        super(setter());
        this.socket = socket;
    }

    @Override
    protected String run() throws Exception {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            logger.debug("Connected to server...");
            out.println("Hello server");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                logger.debug("Client get: " + inputLine);
                out.println("Timestamp: " + System.currentTimeMillis());
            }
        } catch (IOException e) {
            logger.error("Client got exception: " + e);
        } finally {
            socket.close();
        }

        return "Completed";
    }

    @Override
    protected String getFallback() {
        Throwable t = getExecutionException();
        logger.error("Fallback on " + t);

        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Fallback";
    }

    private static Setter setter() {
        return Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("task-cancellation"))
            .andCommandKey(HystrixCommandKey.Factory.asKey("socket-command"))
            .andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter()
                    .withExecutionTimeoutInMilliseconds(5_000)
            );
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 4455;
        DateSocketServer server = new DateSocketServer();
        server.start(port, 700);


        try {
            Future<String> future = new HystrixSocketCommand(new Socket("127.0.0.1", port)).queue();
            logger.debug(future.get());
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            logger.error("SocketCommand exception: " + e.getCause().getMessage());
        }

        TimeUnit.SECONDS.sleep(10);
        System.exit(0);
    }
}
