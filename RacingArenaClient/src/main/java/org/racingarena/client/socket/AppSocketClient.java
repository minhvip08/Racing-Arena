package org.racingarena.client.socket;

import java.util.logging.Logger;

public class AppSocketClient {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("root");
        logger.info("Client started");
        try {
            TCPClient client = new TCPClient(logger);
            client.start();
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }
    }
}
