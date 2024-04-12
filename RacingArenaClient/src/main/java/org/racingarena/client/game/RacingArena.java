package org.racingarena.client.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.racingarena.client.screen.RegistrationScreen;
import org.racingarena.client.socket.GamePlay;
import org.racingarena.client.socket.model.Client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class RacingArena extends Game {
    public SpriteBatch batch;
    public GamePlay gamePlay;
    public Logger logger;
    public Texture carT;
    public Sprite[] carS;

    @Override
    public void create() {
        batch = new SpriteBatch();
        gamePlay = new GamePlay();
        logger = Logger.getLogger("root");
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new Client(logger, gamePlay));
            executor.shutdown();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        carT = new Texture(Gdx.files.classpath("textures/cars.png"));
        carS = new Sprite[Property.NCAR];
        this.setScreen(new RegistrationScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        gamePlay.shutdown();
    }
}
