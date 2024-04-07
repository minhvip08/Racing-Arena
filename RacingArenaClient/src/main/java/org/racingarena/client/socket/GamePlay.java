package org.racingarena.client.socket;

import java.util.logging.Logger;

public class GamePlay extends Thread{
    private boolean isRegistered = false;
    private Logger logger;

    public GamePlay(Logger logger) {
        this.logger = logger;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }


}
