package org.racingarena.client.game;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.racingarena.client.screen.GameScreen;

public class RacingArena extends Game {
    public SpriteBatch batch;
    @Override
    public void create() {
        batch = new SpriteBatch();
        // Add registration screen when it is ready. For now, opening the game screen first.
        //this.setScreen(new RegistrationScreen(this));
        //this.setScreen(new TempScreen(this));
        this.setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
