package org.racingarena.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.racingarena.client.game.PlayerState;
import org.racingarena.client.game.Property;
import org.racingarena.client.game.RacingArena;
import org.racingarena.client.object.Calculation;

import java.util.ArrayList;

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
    private int playerCount = 5;
    // simulate the state of player
    private PlayerState playerState = PlayerState.RACING;
    final Stage stage;
    final Skin skin;
    final TextField answer;
    private int timer;
    private float timeSeconds = 0f;
    private float period = 1f;
    Label timerLabel;
    Calculation calculation;
    Label calculationLabel;

    java.util.List<String> Messages = new ArrayList<String>() {{
        add("Supplier 1");
        add("Supplier 2");
        add("Supplier 3");
        add("Supplier 1");
        add("Supplier 2");
        add("Supplier 3");
        add("Supplier 1");
        add("Supplier 2");
        add("Supplier 3");
        add("Supplier 1");
        add("Supplier 2");
        add("Supplier 3");
    }};
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

        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("orangepeelui/uiskin.json"));

        calculation = new Calculation();

        timer = 10;

        timerLabel =  new Label(Integer.toString(timer), skin, "title");

        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);
        rootTable.bottom();
        stage.addActor(rootTable);

        Table panelTable = new Table();
        panelTable.defaults().growX().fillY();
        Table table = new Table(skin);
        Label label = new Label("Your Calculation", skin, "title");
        table.background("panel-orange");
        table.add(label).colspan(2).padBottom(20);
        table.row();
        calculation.newCalculation();
        calculationLabel = new Label(calculation.getCalculation(), skin, "title");
        table.add(calculationLabel);
        panelTable.add(table);
        answer = new TextField("", skin);
        answer.setSize(150, 40);
        answer.setAlignment(Align.center);
        table.add(answer).width(150);

        table = new Table(skin);
        table.background("panel-orange");
        label = new Label("Countdown", skin, "title");
        table.background("panel-orange");
        table.add(label).padBottom(20);
        table.row();
        table.add(timerLabel);
        panelTable.add(table);

        table = new Table(skin);
        label = new Label("Messeages", skin, "title");
        table.background("panel-orange");
        table.add(label);

        table.row();
        Table subTable = new Table();
        for (String message : Messages) {
            subTable.row();
            subTable.add(new Label(message, skin));
        }
        ScrollPane scrollPane = new ScrollPane(subTable, skin, "android-no-bg");
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        table.add(scrollPane).grow();
        panelTable.add(table);
        rootTable.add(panelTable).maxHeight(100.0f).width(Property.WIDTH);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        timeSeconds += v;
        if (timeSeconds > period){
            timeSeconds-=period;
            timer--;
            if (timer < 0){
                timer = 10;
                calculation.newCalculation();
                calculationLabel.setText(calculation.getCalculation());
            }
            timerLabel.setText(Integer.toString(timer));
        }
        stage.act(v);
        stage.draw();
        //ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(audienceT, 0, 0, Property.TSIZE, Property.ROTATE_ORIGIN, Property.TSIZE * 2, Property.TSIZE, 1, 1, 0, 0, 0, Property.TSIZE * 4, Property.TSIZE * 2, false, true);
        for (int i = 2; i < Property.LRACE_MAX; ++i) {
            game.batch.draw(audienceT, i * Property.TSIZE, 0, Property.TSIZE, Property.ROTATE_ORIGIN, Property.TSIZE, Property.TSIZE, 1, 1, 0, Property.TSIZE * 2, 0, Property.TSIZE * 2, Property.TSIZE * 2, false, true);
        }
        game.batch.draw(audienceT, Property.LRACE_MAX * Property.TSIZE, 0, Property.TSIZE, Property.ROTATE_ORIGIN, Property.TSIZE * 2, Property.TSIZE, 1, 1, 0, 0, 0, Property.TSIZE * 4, Property.TSIZE * 2, true, true);
        for (int i = 1; i <= Property.NCAR; ++i) {
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
        }
        for (int i = 0; i < playerCount; ++i) {
            game.batch.draw(carS[i], carS[i].getX(), carS[i].getY(), Property.ROTATE_ORIGIN, Property.ROTATE_ORIGIN, Property.TSIZE, Property.TSIZE, 1, 1, 90);
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
        stage.dispose();
        carT.dispose();
        carGreyT.dispose();
        roadT.dispose();
        edgeT.dispose();
        finish1T.dispose();
        finish2T.dispose();
        audienceT.dispose();
    }
}
