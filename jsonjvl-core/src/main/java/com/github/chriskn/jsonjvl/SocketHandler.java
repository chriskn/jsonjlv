package com.github.chriskn.jsonjvl;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.json.JSONObject;

public class SocketHandler implements Runnable {

    private final Logger logger = Logger.getLogger(this.getClass());
    private final Socket socket;
    private EventDeserializer deserializer;

    String host;
    int port;

    public SocketHandler(Socket socket, String host, int port) {
        this.socket = socket;
        deserializer = new EventDeserializer();
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        DataInputStream inputStream = null;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            while (!socket.isClosed()) {
                int length = inputStream.readInt();
                byte[] data = new byte[length];
                inputStream.read(data);
                String dataString = new String(data, "UTF-8");
                LoggingEvent event = deserializer.deserialize(new JSONObject(dataString));
                send(event);
            }
        } catch (EOFException e) {
            // stream ended, all good
        } catch (IOException e) {
            logger.error(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
//                    socket.close();
                } catch (IOException e) {
                    logger.error("Error while closing socket", e);
                }
            }
        }
    }

    private void send(LoggingEvent event) {
        try {
            Socket socket = new Socket(host, port);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(event);
            outputStream.flush();
            socket.close();
        } catch (IOException e) {
            logger.error("Error while sending LoggingEvent. LoggingEvent will not be send", e);
        }
    }
}
