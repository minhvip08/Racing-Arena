package org.racingarena.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.lwjgl.opengl.GL20;
import org.racingarena.client.game.RacingArena;

public class RegistrationScreen implements Screen {
    final RacingArena game;
    private Stage stage;
    private TextField usernameField;

    public RegistrationScreen(final RacingArena game) {
        this.game = game;

        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("Skin/glassy/skin/glassy-ui.json"));

        TextButton btnReg = new TextButton("Register", skin);
        btnReg.setPosition((Gdx.graphics.getWidth() / 2f) - 150, (Gdx.graphics.getHeight() / 2f) - 60);
        btnReg.setSize(300, 60);
        btnReg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                registerButtonClicked();
            }
        });

        usernameField = new TextField("", skin);
        usernameField.setPosition((Gdx.graphics.getWidth() / 2f) - 150, (Gdx.graphics.getHeight() / 2f) + 30);
        usernameField.setSize(300, 40);

        stage.addActor(usernameField);
        stage.addActor(btnReg);
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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
        game.dispose();
        stage.dispose();
    }
}
