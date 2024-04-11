package org.racingarena.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import org.racingarena.client.game.RacingArena;
import org.racingarena.client.socket.ConstantVariable;

import java.awt.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RegistrationScreen implements Screen {
    final RacingArena game;
    final Stage stage;
    final TextField usernameField;

    final Skin skin;

    Label validLabel;

    boolean canRegis = true;

    public RegistrationScreen(final RacingArena game) {
        this.game = game;

        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.classpath("orangepeelui/uiskin.json"));

        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Label label = new Label("Racing Arena", skin, "title");
        label.setAlignment(Align.top);
        rootTable.add(label).colspan(2).growX();

        rootTable.row();
        usernameField = new TextField("", skin);
        usernameField.setSize(300, 40);
        usernameField.setAlignment(Align.center);
        rootTable.add(usernameField).width(300).colspan(1).growX();;
        usernameField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    registerButtonClicked();
                    return true;
                }
                return false;
            }
        });

        rootTable.row();
        validLabel = new Label("Invalid username", skin);
        validLabel.setAlignment(Align.top);
        validLabel.setVisible(!game.gamePlay.isValidName());
        validLabel.setColor(Color.RED);
        rootTable.add(validLabel).colspan(2).growX();

        rootTable.row();
        TextButton btnReg = new TextButton("Register", skin);
        btnReg.setSize(150, 60);
        btnReg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                registerButtonClicked();
            }
        });

        rootTable.add(btnReg).width(150).colspan(1).padTop(10.0f).growX();
    }

    private void registerButtonClicked() {
        String username = usernameField.getText();
        game.gamePlay.setUsername(username);
        try {
            game.gamePlay.barrier.await(ConstantVariable.TIMEOUT, TimeUnit.MILLISECONDS);
        }
        catch (Exception e) {
            if (e instanceof TimeoutException) {
                validLabel.setText("Registration denied from the sever");
                validLabel.setVisible(true);
                canRegis = false;
                System.out.println("registerButtonClicked(): timeout");
            }
        }
    }

    private boolean isValidNickname(String nickname) {
        return nickname.matches("[a-zA-Z0-9_]{1,10}");
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        if (game.gamePlay.getRegistered()) {
            game.setScreen(new WaitingScreen(game));
        }
        if (canRegis) validLabel.setVisible(!game.gamePlay.isValidName());
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
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
