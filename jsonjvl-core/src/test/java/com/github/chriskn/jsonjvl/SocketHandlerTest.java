package com.github.chriskn.jsonjvl;

import org.junit.Test;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;

public class SocketHandlerTest {

    SocketHandler sut;
    Thread thread;
    ExecutorService executer = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    Socket socket;

    @Test
    public void test() throws Exception {
        final String host = "127.0.0.1";
        final int outgoingPort = TestUtils.getAvailablePort();
        final int incomingPort = TestUtils.getAvailablePort(outgoingPort);
        final ServerSocket server = new ServerSocket(incomingPort);
        TestReceiver receiver = new TestReceiver(outgoingPort);
        receiver.start();
        TestSender sender = new TestSender(host, incomingPort);
        thread = new Thread() {
            public void run() {
                while (!server.isClosed()) {
                    try {
                        socket = server.accept();
                        sut = new SocketHandler(socket, host, outgoingPort);
                        executer.execute(sut);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
        sender.sendJson();
        Thread.sleep(2000);
        LoggingEvent result = receiver.receivedData.pop();
        System.out.println(result);
        server.close();
    }

    @After
    public void tearDown() throws IOException {
        executer.shutdown();
        thread.interrupt();
        socket.close();
        sut = null;
    }

}
