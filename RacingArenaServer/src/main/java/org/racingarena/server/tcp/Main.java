package org.racingarena.server.tcp;

import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("root");
        logger.info("Server started");
        try {
            TCPServer server = new TCPServer(logger);
            server.start();
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }


    }
}