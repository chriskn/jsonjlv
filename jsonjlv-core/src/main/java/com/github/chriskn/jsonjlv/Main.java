package com.github.chriskn.jsonjlv;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final int INCOMING_PORT = 1223;
    private static final int OUTGOING_PORT = 4000;
    private static final String IP = "127.0.0.1";
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        try {
            Server server = new Server(INCOMING_PORT, OUTGOING_PORT, IP);
            server.start();
        } catch (Exception e) {
           logger.error("Startup error", e);
        } 
        

    }

}
