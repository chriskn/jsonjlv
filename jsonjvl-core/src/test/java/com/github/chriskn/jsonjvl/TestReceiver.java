package com.github.chriskn.jsonjvl;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Stack;
import java.util.concurrent.RejectedExecutionException;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.json.JSONObject;

public class TestReceiver extends Thread {

    private ServerSocket serverSocket;
    Stack<LoggingEvent> receivedData;

    public TestReceiver(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        receivedData = new Stack<>(); 
    }

    @Override
    public void run() {
        while (!serverSocket.isClosed()) {
            try {
                final Socket socket = serverSocket.accept();
                socket.setSoTimeout(5000);
                receive(socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void receive(Socket socket) {
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            while (!socket.isClosed()) {
                LoggingEvent event = (LoggingEvent)inputStream.readObject(); 
                receivedData.push(event); 
            }
        } catch (Exception e) {
        }
    }
}
