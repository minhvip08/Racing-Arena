package org.racingarena.client.socket;

import org.racingarena.client.socket.model.Client;

import java.util.logging.Logger;

public class AppSocketClient {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("root");
        logger.info("Client started");
        try {
            Client client = new Client(logger);
            client.run();
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }
    }
}
