package org.racingarena.client.socket;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class GamePlay {
    private int round;
    private boolean registered;
    private String username;
    private int index;
    private int playerCount;
    private String status;
    private ArrayList<Player> players;
    public final CyclicBarrier barrier;

    public synchronized String getUsername() {
        return username;
    }

    public synchronized void setUsername(String username) {
        this.username = username;
    }

    public synchronized boolean getRegistered() {
        return registered;
    }

    public synchronized void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public synchronized ArrayList<Player> getPlayers() {
        return players;
    }

    public synchronized void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getRound() {
        return round;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public GamePlay() {
        this.barrier = new CyclicBarrier(2);
        this.username = null;
        this.registered = false;
        this.status = null;
        this.players = null;
        this.round = 0;
        this.playerCount = 0;
    }

    public int getPlayerCount() {
        return this.playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }
}
