package org.racingarena.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.racingarena.client.game.PlayerState;
import org.racingarena.client.game.Property;
import org.racingarena.client.game.RacingArena;

import java.util.Arrays;

public class GameScreen implements Screen {
    final RacingArena game;
    OrthographicCamera camera;
    FitViewport viewport;
    Texture carT;
    Texture carGreyT;
    Texture roadT;
    Texture edgeT;
    Texture finish1T;
    Texture finish2T;
    Texture audienceT;
    Sprite[] carS;
    TextureRegion audienceTR;
    // distance travel
    private int distance = 0;
    // test variables only, could change later
    private int raceLength = 5;
    private int playerCount = 10;
    // simulate the state of player
    private PlayerState playerState = PlayerState.RACING;
    public GameScreen(final RacingArena game) {
        this.game = game;
        carT = new Texture(Gdx.files.classpath("textures/cars.png"));
        carGreyT = new Texture(Gdx.files.classpath("textures/cars_grey.png"));
        roadT = new Texture(Gdx.files.classpath("textures/road.png"));
        edgeT = new Texture(Gdx.files.classpath("textures/edge.png"));
        finish1T = new Texture(Gdx.files.classpath("textures/finish_line.png"));
        finish2T = new Texture(Gdx.files.classpath("textures/finish_line_inverted.png"));
        audienceT = new Texture(Gdx.files.classpath("textures/audience.png"), true);
        carS = new Sprite[Property.NCAR];
        for (int i = 0; i < Property.TCAR_NCOL; ++i) {
            carS[i] = new Sprite(carT, i * 32, 0, 32, 32);
            carS[i].flip(false, true);
            carS[i].setPosition(Property.TSIZE, Property.TSIZE * (i + 1));
            carS[i + Property.TCAR_NCOL] = new Sprite(carT, i * 32, 32, 32, 32);
            carS[i + Property.TCAR_NCOL].flip(false, true);
            carS[i + Property.TCAR_NCOL].setPosition(Property.TSIZE, Property.TSIZE * (i + 1 + Property.TCAR_NCOL));
        }
        audienceTR = new TextureRegion(audienceT, Property.TSIZE * 2, 0, Property.TSIZE * 4, Property.TSIZE * 2);
        audienceTR.flip(false, true);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FitViewport(Property.WIDTH, Property.HEIGHT, camera);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(audienceT, 0, 0, Property.TSIZE, Property.ROTATE_ORIGIN, Property.TSIZE * 2, Property.TSIZE, 1, 1, 0, 0, 0, Property.TSIZE * 4, Property.TSIZE * 2, false, true);
        for (int i = 2; i < Property.LRACE_MAX; ++i) {
            game.batch.draw(audienceT, i * Property.TSIZE, 0, Property.TSIZE, Property.ROTATE_ORIGIN, Property.TSIZE, Property.TSIZE, 1, 1, 0, Property.TSIZE * 2, 0, Property.TSIZE * 2, Property.TSIZE * 2, false, true);
        }
        game.batch.draw(audienceT, Property.LRACE_MAX * Property.TSIZE, 0, Property.TSIZE, Property.ROTATE_ORIGIN, Property.TSIZE * 2, Property.TSIZE, 1, 1, 0, 0, 0, Property.TSIZE * 4, Property.TSIZE * 2, true, true);
        for (int i = 1; i <= playerCount; ++i) {
            game.batch.draw(edgeT, 0, i * Property.TSIZE, Property.TSIZE, Property.TSIZE);
            for (int j = 1; j < raceLength; ++j) {
                game.batch.draw(roadT, j * Property.TSIZE, i * Property.TSIZE, Property.TSIZE, Property.TSIZE);
            }
            if (i % 2 == 0) {
                game.batch.draw(finish1T, raceLength * Property.TSIZE, i * Property.TSIZE, Property.TSIZE, Property.TSIZE);
            } else {
                game.batch.draw(finish2T, raceLength * Property.TSIZE, i * Property.TSIZE, Property.TSIZE, Property.TSIZE);
            }
            for (int j = raceLength + 1; j <= Property.LRACE_MAX; ++j) {
                game.batch.draw(roadT, j * Property.TSIZE, i * Property.TSIZE, Property.TSIZE, Property.TSIZE);
            }
            game.batch.draw(edgeT, (Property.LRACE_MAX + 1) * Property.TSIZE, i * Property.TSIZE, Property.TSIZE, Property.TSIZE);
            game.batch.draw(carS[i - 1], carS[i - 1].getX(), carS[i - 1].getY(), Property.ROTATE_ORIGIN, Property.ROTATE_ORIGIN, Property.TSIZE, Property.TSIZE, 1, 1, 90);
        }
        game.batch.end();
        // RIGHT is equivalent to the server announcing the result
        if (playerState == PlayerState.RACING && Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerState = PlayerState.WAITING;
            distance = Property.TSIZE;
        }
        if (playerState == PlayerState.WAITING) {
            // Simulate server interruption with N key pressed signal
            if (Gdx.input.isKeyPressed(Input.Keys.N)) {
                playerState = PlayerState.RACING;
                distance = 0;
                // Should forcefully teleport the car forward
                carS[0].setX(carS[0].getX() + Property.TSIZE - carS[0].getX() % Property.TSIZE);
            }
            else if (distance == 0) {
                playerState = PlayerState.RACING;
            }
            else {
                distance -= Property.SPEED;
                // increment to move forward, decrement to move backward
                carS[0].setX(carS[0].getX() + Property.SPEED);
            }
        }
        // Simulate player disqualification with D key pressed signal
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerState = PlayerState.DISQUALIFIED;
            carS[0].setTexture(carGreyT);
        }
    }

    @Override
    public void resize(int i, int i1) {
        viewport.update(i, i1);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        carT.dispose();
        roadT.dispose();
        edgeT.dispose();
        finish1T.dispose();
        finish2T.dispose();
        audienceT.dispose();
    }
}
