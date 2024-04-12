package org.racingarena.server.tcp;

import org.racingarena.server.tcp.controller.TCPServer;
import org.racingarena.server.tcp.model.WaitingRoom;

import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("root");
        if (args.length > 1) {
            System.out.println("Server is running with args: " + args[0] + " " + args[1]);
        }
        int numberOfPlayers = 0;
        numberOfPlayers = args.length > 0 ? Integer.parseInt(args[0]) : 0;
        Scanner scanner = new Scanner(System.in);
        while (numberOfPlayers < 2 || numberOfPlayers > 10) {
            System.out.println("Enter number of players: (2<=N<=10)");
            numberOfPlayers = scanner.nextInt();
        }
        int raceLength = 0;
        raceLength = args.length > 1 ? Integer.parseInt(args[1]) : 0;
        while (raceLength < 4 || raceLength > 25) {
            System.out.println("Enter the length of the race: (3<N<26)");
            raceLength = scanner.nextInt();
        }
        WaitingRoom.MAX_PLAYER = numberOfPlayers;
        WaitingRoom.MAX_SCORE = raceLength;
        try {
            TCPServer server = new TCPServer(logger);
            server.run();
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }


    }
}