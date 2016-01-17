package com.github.chriskn.jsonjvl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server extends Thread {

    private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    final Logger logger = LoggerFactory.getLogger(getClass());
    final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    ServerSocket serverSocket;
    SocketHandler handler;
    private int outgoingPort;
    private String ip;

    public Server(int incomingPort, int outgoingPort, String ip) throws Exception {
        if (incomingPort < 0 || outgoingPort < 0) {
            throw new IllegalArgumentException("Port value should be a positive number. Current incoming port: "
                    + incomingPort + ", current outgoing port: " + outgoingPort);
        }
        if (!ip.matches(IPADDRESS_PATTERN)) {
            throw new IllegalArgumentException("Invalid ip address: " + ip);
        }
        this.outgoingPort = outgoingPort;
        this.ip = ip;
        try {
            serverSocket = new ServerSocket(incomingPort);
        } catch (Exception e) {
            logger.error("Could not listen on port: " + incomingPort, e);
            throw e;
        }
    }

    @Override
    public void run() {
        logger.debug("Waiting for a new connection on port " + serverSocket.getLocalPort() + ". Will send to " + ip
                + ":" + outgoingPort);
        while (!serverSocket.isClosed()) {
            try {
                final Socket socket = serverSocket.accept();
                socket.setSoTimeout(5000);
                logger.debug("Connection has been accepted from " + socket.getInetAddress().getHostName());
                handler = new SocketHandler(socket, ip, outgoingPort);
                executor.execute(handler);
            } catch (RejectedExecutionException e) {
                logger.warn("Execution rejected", e);
            } catch (IOException e) {
                logger.warn("Failed to accept a new connection: server socket was closed.", e);
            }
        }
    }

    public void shutdown() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logger.error("IOException occurred while closing server socket:", e);
        } finally {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                logger.error("InterruptedException occurred while shutting down server executor.", e);
            }
        }
        interrupt();
        logger.debug("Server was stopped");
    }
}
