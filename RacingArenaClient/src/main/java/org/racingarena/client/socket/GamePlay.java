package org.racingarena.client.socket;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class GamePlay {
    private boolean registered;
    private String username;
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

    public GamePlay() {
        this.barrier = new CyclicBarrier(2);
        this.username = null;
        this.registered = false;
        this.status = null;
        this.players = null;
    }
}
