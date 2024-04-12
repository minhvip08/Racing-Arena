package org.racingarena.server.tcp;

import org.racingarena.server.tcp.controller.TCPServer;
import org.racingarena.server.tcp.model.WaitingRoom;

import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("root");
        if (args.length > 0) {
            System.out.println("Server is running with args: " + args[0]);
        }
        int numberOfPlayers = 0;
        numberOfPlayers = args.length > 0 ? Integer.parseInt(args[0]) : 0;
        while (numberOfPlayers < 2 || numberOfPlayers > 10) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter number of players: (2<=N<=10)");
            numberOfPlayers = scanner.nextInt();
        }
        WaitingRoom.MAX_PLAYER = numberOfPlayers;
        try {
            TCPServer server = new TCPServer(logger);
            server.run();
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }


    }
}