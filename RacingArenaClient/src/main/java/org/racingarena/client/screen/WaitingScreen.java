package org.racingarena.client.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import org.racingarena.client.game.RacingArena;
import org.racingarena.client.socket.Status;

import java.util.Objects;

public class WaitingScreen implements Screen {
    final RacingArena game;

    public WaitingScreen(final RacingArena game) {
        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);
        if (Objects.equals(game.gamePlay.getStatus(), Status.CLIENT_READY)) {
            game.setScreen(new GameScreen(game));
        }
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

    }
}
