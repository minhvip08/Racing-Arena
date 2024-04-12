package org.racingarena.client;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import org.racingarena.client.game.Property;
import org.racingarena.client.game.RacingArena;
import org.racingarena.client.socket.model.Client;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Racing Arena client");
        config.setWindowedMode(Property.WIDTH, Property.HEIGHT);
        config.useVsync(true);
        config.setForegroundFPS(Property.FPS);
        config.setWindowIcon(Files.FileType.Internal, "icon.png");
        new Lwjgl3Application(new RacingArena(), config);
    }
}