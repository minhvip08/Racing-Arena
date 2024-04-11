package org.racingarena.client.socket;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class GamePlay {
    private boolean registered;
    private String username;
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

    public GamePlay() {
        this.barrier = new CyclicBarrier(2);
        this.username = null;
        this.registered = false;
    }
}
