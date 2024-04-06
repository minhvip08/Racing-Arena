package org.racingarena.server.tcp.model;

import org.racingarena.server.tcp.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WaitingRoom {
    public static int MAX_PLAYER = 10;
    public volatile List<Player> players;
    public volatile Logger logger;

    public Boolean isFull() {
        return players.size() == MAX_PLAYER;
    }

    public synchronized Boolean addPlayer(Player player) {
        if (isFull() || !players.contains(player)) {
            logger.info("Player " + player.getName() + " cannot join the waiting room");
            return false;
        }

        logger.info("Player " + player.getName() + " joined the waiting room");
        players.add(player);
        return true;
    }

    public void removePlayer(Player player) {
        logger.info("Player " + player.getName() + " left the waiting room");
        players.remove(player);
    }

    public List<Player> getPlayerRegistered() {
        List<Player> registeredPlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.isRegistered()) {
                registeredPlayers.add(player);
            }
        }
        return registeredPlayers;
    }

//    public void broadcast(String message) {
//        for (Player player : players) {
//            player.writeTheBuffer(message);
//        }
//    }
//
//    public void broadcastExcept(String message, Player player) {
//        for (Player p : players) {
//            if (p != player) {
//                p.writeTheBuffer(message);
//            }
//        }
//    }


}
