package org.sdoroshenko.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A simple server that sends current date.
 */
public class DateSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(DateSocketServer.class);

    private Thread t;

    public void start(final int port, final long timeout) {
        this.t = new Thread() {

            ServerSocket serverSocket;

            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(port);
                    logger.info("The date server is running on: " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());
                    ExecutorService executor = Executors.newFixedThreadPool(5);
                    while (true) {
                        Socket socket = serverSocket.accept();
                        logger.info("New connection: " + socket.toString());

                        executor.submit(() -> {
                            try {
                                logger.info("In executor");
                                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                logger.info("Trying to sent a message");
                                out.println("Hello client");
                                String inputLine;
                                while ((inputLine = in.readLine()) != null) {
                                    logger.info("Server get: " + inputLine);
                                    out.println(new Date().toString());
                                    TimeUnit.MILLISECONDS.sleep(timeout);
                                }
                            } catch (InterruptedException e) {
                                logger.error("Date server was interrupted : ", e);
                                Thread.currentThread().interrupt();
                            } catch (IOException e) {
                                logger.error("Date server connection error: ", e.getMessage());
                            } finally {
                                try {
                                    logger.info("Closing client socket");
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    logger.error("Date server starting error: ", e);
                }
            }

            @Override
            public void interrupt() {
                try {
                    logger.info("Client canceled...");
                    serverSocket.close();
                } catch (IOException e) {
                    logger.error("Client got exception: " + e);
                } finally {
                    super.interrupt();
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }

    public void stop() {
        logger.info("Stopping server...");
        t.interrupt();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        DateSocketServer server = new DateSocketServer();
        server.start(4455, 700);
        System.in.read();
        server.stop();
    }
}
