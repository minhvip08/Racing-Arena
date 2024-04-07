package org.racingarena.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import org.racingarena.client.game.RacingArena;

public class RegistrationScreen implements Screen {
    final RacingArena game;
    final Stage stage;
    final TextField usernameField;

    final Skin skin;

    public RegistrationScreen(final RacingArena game) {
        this.game = game;

        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("orangepeelui/uiskin.json"));

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
        String username = usernameField.getText().trim();
        if (isValidNickname(username)) {
            Gdx.app.log("Registration", "Username: " + username);
        } else {
            Gdx.app.log("Registration", "Invalid username format");
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
