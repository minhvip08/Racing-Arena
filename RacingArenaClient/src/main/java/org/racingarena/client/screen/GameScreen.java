package org.racingarena.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import org.racingarena.client.game.RacingArena;

public class GameScreen implements Screen {
    final RacingArena game;
    OrthographicCamera camera;
    Texture carT;
    Texture roadT;
    TextureRegion[] carTR;

    public GameScreen(final RacingArena game) {
        this.game = game;
        carT = new Texture(Gdx.files.internal("RacingArenaClient/assets/cars.png"));
        roadT = new Texture(Gdx.files.internal("RacingArenaClient/assets/road.png"));
        carTR = new TextureRegion[10];
        for (int i = 0; i < 5; ++i) {
            carTR[i] = new TextureRegion(carT, i * 32, 0, 32, 32);
            carTR[i + 5] = new TextureRegion(carT, i * 32, 32, 32, 32);
        }
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
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
        game.batch.draw(roadT, 0, 0);
        game.batch.draw(carTR[0], 0, 0, 16, 16, 32, 32, 1, 1, -90);
        game.batch.end();
    }

    @Override
    public void resize(int i, int i1) {

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
    }
}
