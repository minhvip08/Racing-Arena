package org.racingarena.client.game;
import com.badlogic.gdx.Game;
import org.racingarena.client.screen.GameScreen;

public class RacingArena extends Game {
    @Override
    public void create() {
        // Add registration screen when it is ready. For now, opening the game screen first.
        // this.setScreen(new RegistrationScreen(this));
        this.setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }
}
