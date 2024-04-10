package org.racingarena.client.socket;

public class GamePlay {
    private boolean registered;
    private String username;


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

    public GamePlay() {
        this.username = null;
        this.registered = false;
    }
}
