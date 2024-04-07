package org.racingarena.server.tcp.model;

import org.racingarena.server.tcp.model.Player;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.logging.Logger;

public class WaitingRoom {
    public static int MAX_PLAYER = 1;
    public volatile Map<SelectionKey, Player> players;
    public volatile Logger logger;

    public WaitingRoom(Logger logger) {
        this.logger = logger;
        players = new LinkedHashMap<>();
    }

    public Boolean isFull() {
        return players.size() == MAX_PLAYER;
    }

    public synchronized Boolean addPlayer(Player player) {
        if (isFull() || players.containsValue(player)) {
            logger.info("Player " + player.getName() + " cannot join the waiting room");
            return false;
        }

        logger.info("Player " + player.getName() + " joined the waiting room");
        players.put(player.getKey(), player);
        return true;
    }

    public void removePlayer(Player player) {
        logger.info("Player " + player.getName() + " left the waiting room");
        players.remove(player.getKey());
    }

    public synchronized List<Player> getPlayerRegistered() {
        List<Player> registeredPlayers = new ArrayList<>();
        for (Player player : players.values()) {
            if (player.isRegistered()) {
                registeredPlayers.add(player);
            }
        }
        return registeredPlayers;
    }

    public synchronized void broadcast(String message) {
        for (Player player : players.values()) {
            player.writeTheBuffer(message);
        }
    }

    public synchronized void broadcastRegistered(String message) {
        for (Player player : players.values()) {
            if (player.isRegistered() && !player.isEliminated()) {
                player.writeTheBuffer(message);
            }
        }
    }

    public synchronized Player getHighestScorePlayer() {
        Player highestScorePlayer = null;
        for (Player player : players.values()) {
            if (highestScorePlayer == null || player.getScore() > highestScorePlayer.getScore()) {
                highestScorePlayer = player;
            }
        }
        return highestScorePlayer;
    }

//    Get all players who are registered and not eliminated, sorted by timestamp
    public synchronized List<Player> getSortedPlayersByTimestamp() {
        List<Player> sortedPlayers = new ArrayList<>(getReadyPlayers());
        sortedPlayers.sort(Comparator.comparing(Player::getTimestamp));
        return sortedPlayers;
    }

// Get all players who are registered and not eliminated
    public synchronized List<Player> getReadyPlayers() {
        List<Player> readyPlayers = new ArrayList<>();
        for (Player player : players.values()) {
            if (player.isRegistered() && !player.isEliminated())
                readyPlayers.add(player);
        }
        return readyPlayers;
    }

    public synchronized Player getPlayerByName(String name) {
        for (Player player : players.values()) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    public synchronized Map<SelectionKey, Player> getPlayers() {
        return players;
    }

    public synchronized Boolean isPlayerRegistered(String name) {
        for (Player player : players.values()) {
            if (player.getName().isEmpty())
                return false;
            if ( player.getName().equals(name)) {
                return player.isRegistered();
            }
        }
        return false;
    }

    public synchronized Boolean isPlayerExist(String name) {
        for (Player player : players.values()) {
            if (player.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void resetPlayers() {
        for (Player player : players.values()) {
            player.reset();
        }
    }

    public synchronized void resetRound() {
        for (Player player : players.values()) {
            player.resetRound();
        }
    }
//    public synchronized void closeOnePlayer(Player player) {
//        player.closeTheClient();
//    }


//    public void broadcastExcept(String message, Player player) {
//        for (Player p : players) {
//            if (p != player) {
//                p.writeTheBuffer(message);
//            }
//        }
//    }


}
