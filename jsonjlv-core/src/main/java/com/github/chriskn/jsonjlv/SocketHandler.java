package com.github.chriskn.jsonjlv;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
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
        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {
            while (!socket.isClosed()) {
                int length = inputStream.readInt();
                String dataString = slurp(inputStream, length);
                LoggingEvent event = deserializer.deserialize(new JSONObject(dataString));
                send(event);
            }
        } catch (EOFException e) {
            // stream ended, all good
        } catch (IOException e) {
            logger.error(e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("Error while closing socket", e);
                }
            }
        }
    }

    private String slurp(final InputStream is, final int bufferSize) {
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try (Reader in = new InputStreamReader(is, "UTF-8")) {
            for (;;) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
        } catch (UnsupportedEncodingException ex) {
            logger.error(ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return out.toString();
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
