package org.racingarena.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import org.racingarena.client.game.RacingArena;

public class FinishedScreen implements Screen {
    final RacingArena game;
    final Stage stage;
    final Skin skin;
    private float timeSeconds = 0f;
    private float period = 1f;

    private int timer = 10;

    Label timeLabel;

    public FinishedScreen(final RacingArena game) {
        this.game = game;
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.classpath("orangepeelui/uiskin.json"));

        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Label label = new Label("GAME OVER", skin, "title");
        label.setAlignment(Align.top);
        rootTable.add(label).colspan(2).growX();

        rootTable.row();

        label = new Label("The window will close in", skin);
        label.setAlignment(Align.top);
        rootTable.add(label).colspan(2).growX();

        rootTable.row();
        timeLabel = new Label("10", skin);
        timeLabel.setAlignment(Align.top);
        rootTable.add(timeLabel).colspan(2).growX();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        timeSeconds += delta;
        if (timeSeconds > period) {
            timeSeconds -= period;
            timer--;
            if (timer <= 0) {
                System.exit(0);
            }
        }
        else timeLabel.setText(Integer.toString(timer));
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        skin.dispose();
    }
}
