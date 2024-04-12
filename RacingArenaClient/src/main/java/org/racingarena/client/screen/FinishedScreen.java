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
import org.racingarena.client.socket.Status;
import org.racingarena.client.socket.Winner;

import java.util.Objects;

public class FinishedScreen implements Screen {
    final RacingArena game;
    final Stage stage;
    final Skin skin;
    final Winner winner;
    private float timeSeconds = 0f;
    private float period = 1f;

    private int timer = 10;

    Label timeLabel;

    Label messageLabel;

    public FinishedScreen(final RacingArena game) {
        this.game = game;
        this.stage = new Stage();
        this.winner = game.gamePlay.getWinner();

        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.classpath("orangepeelui/uiskin.json"));

        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        String Msg;

        if (game.gamePlay.getWinner() != null){
            Msg = "Congratulation! you are the winner:))";
        }
        else{
            Msg = "You got eliminated!";
        }

        rootTable.row();
        Label label = new Label("GAME OVER", skin, "title");
        label.setAlignment(Align.top);
        rootTable.add(label).colspan(2).growX();

        rootTable.row();
        messageLabel = new Label(Msg, skin, "title");
        messageLabel.setAlignment(Align.top);
        rootTable.add(messageLabel).colspan(2).growX();

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
