package org.racingarena.client;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import org.racingarena.client.game.Property;
import org.racingarena.client.game.RacingArena;

public class Main {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Racing Arena client");
        config.setWindowedMode(Property.WIDTH, Property.HEIGHT);
        config.useVsync(true);
        config.setForegroundFPS(Property.FPS);
        new Lwjgl3Application(new RacingArena(), config);
    }
}