package org.racingarena.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.racingarena.client.game.PlayerState;
import org.racingarena.client.game.Property;
import org.racingarena.client.game.RacingArena;
import org.racingarena.client.socket.Player;
import org.racingarena.client.socket.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
    Texture arrowT;
    Sprite[] carS;
    TextureRegion audienceTR;
    BitmapFont font;
    // distance travel
    private float[] distances;
    private int[] scores;
    private final int playerCount;
    private final int round;
    private final int index;
    // simulate the state of player
    private PlayerState playerState = PlayerState.RACING;
    final Stage stage;
    final Skin skin;
    final TextField answer;
    private float timeSeconds = 0f;
    private float period = 1f;
    private int timer;
    Label timerLabel;
    Label calculationLabel;
    String Messages = "Enter your answer";
    Label messageLabel;
    public GameScreen(final RacingArena game) {
        this.game = game;
        System.out.println("Game is ready to start haha");
        carT = new Texture(Gdx.files.classpath("textures/cars.png"));
        carGreyT = new Texture(Gdx.files.classpath("textures/cars_grey.png"));
        roadT = new Texture(Gdx.files.classpath("textures/road.png"));
        edgeT = new Texture(Gdx.files.classpath("textures/edge.png"));
        finish1T = new Texture(Gdx.files.classpath("textures/finish_line.png"));
        finish2T = new Texture(Gdx.files.classpath("textures/finish_line_inverted.png"));
        audienceT = new Texture(Gdx.files.classpath("textures/audience.png"), true);
        carS = new Sprite[Property.NCAR];
        font = new BitmapFont(true);
        arrowT = new Texture(Gdx.files.classpath("textures/arrow.png"));
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

        skin = new Skin(Gdx.files.classpath("orangepeelui/uiskin.json"));

        timerLabel =  new Label(Integer.toString(game.gamePlay.getQuestionNAnswer().getDuration()), skin, "title");

        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);
        rootTable.bottom();
        stage.addActor(rootTable);

        Table panelTable = new Table();
        panelTable.defaults().growX().fillY();
        Table table = new Table(skin);
        Label label = new Label("Your Calculation", skin, "title");
        table.background("panel-orange");
        table.add(label).colspan(2).padTop(10.0f);
        table.row();
        calculationLabel = new Label(game.gamePlay.getQuestionNAnswer().getQuestion(), skin, "title");
        table.add(calculationLabel);
        panelTable.add(table);
        answer = new TextField("", skin);
        answer.setSize(150, 40);
        answer.setAlignment(Align.center);
        table.add(answer).width(150);
        table.row();
        TextButton btnReg = new TextButton("Submit", skin);
        btnReg.setSize(100, 20);
        btnReg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                submitButtonClicked();
            }
        });
        table.add(btnReg).width(100).colspan(2).padTop(10.0f).growX();

        table = new Table(skin);
        table.background("panel-orange");
        label = new Label("Countdown", skin, "title");
        table.background("panel-orange");
        table.add(label);
        table.row();
        table.add(timerLabel);
        panelTable.add(table);

        table = new Table(skin);
        label = new Label("Message", skin, "title");
        table.background("panel-orange");
        table.add(label);

        table.row();
        messageLabel =  new Label(Messages, skin);
        table.add(messageLabel);
        panelTable.add(table).growY();
        rootTable.add(panelTable).maxHeight(100.0f).width(Property.WIDTH);

        playerCount = game.gamePlay.getPlayerCount();
        round = game.gamePlay.getRound();
        index = game.gamePlay.getIndex();
        distances = new float[playerCount];
        scores = new int[playerCount];
        font.setUseIntegerPositions(false);
        Arrays.fill(distances, 0f);
        Arrays.fill(scores, 1);
    }

    private void submitButtonClicked() {
        try{
            int Answer = Integer.parseInt(answer.getText().trim());
            game.gamePlay.getQuestionNAnswer().setAnswer(Answer);
            game.gamePlay.setSubmit(true);
            game.gamePlay.setReceived(false);
            Messages = "Waiting for the result";
        } catch (NumberFormatException e){
            Messages = "Please enter a number";
        }
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        timeSeconds += v;
        if (timeSeconds > period){
            timeSeconds-=period;
            timer--;
            if (timer < 0 && game.gamePlay.isNewQuestion() && !game.gamePlay.isSubmit()){
                timer = game.gamePlay.getQuestionNAnswer().getDuration();
                calculationLabel.setText(game.gamePlay.getQuestionNAnswer().getQuestion());
                Messages = "Enter your answer";
            }
            else if (timer < 0){
                timerLabel.setText("0");
                Messages = "Waiting for the question";
            }
            else timerLabel.setText(Integer.toString(timer));
        }
        messageLabel.setText(Messages);
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
            for (int j = 1; j < round; ++j) {
                game.batch.draw(roadT, j * Property.TSIZE, i * Property.TSIZE, Property.TSIZE, Property.TSIZE);
            }
            if (i % 2 == 0) {
                game.batch.draw(finish1T, round * Property.TSIZE, i * Property.TSIZE, Property.TSIZE, Property.TSIZE);
            } else {
                game.batch.draw(finish2T, round * Property.TSIZE, i * Property.TSIZE, Property.TSIZE, Property.TSIZE);
            }
            for (int j = round + 1; j <= Property.LRACE_MAX; ++j) {
                game.batch.draw(roadT, j * Property.TSIZE, i * Property.TSIZE, Property.TSIZE, Property.TSIZE);
            }
            game.batch.draw(edgeT, (Property.LRACE_MAX + 1) * Property.TSIZE, i * Property.TSIZE, Property.TSIZE, Property.TSIZE);
        }
        game.batch.draw(arrowT, 0, (index + 1) * Property.TSIZE, Property.TSIZE, Property.TSIZE);
        for (int i = 0; i < playerCount; ++i) {
            game.batch.draw(carS[i], carS[i].getX(), carS[i].getY(), Property.ROTATE_ORIGIN, Property.ROTATE_ORIGIN, Property.TSIZE, Property.TSIZE, 1, 1, 90);
            font.draw(game.batch, String.valueOf(scores[i]), (Property.LRACE_MAX + 1.25f) * Property.TSIZE, (i + 1.1f) * Property.TSIZE);
        }
        game.batch.end();
        for (int i = 0; i < playerCount; ++i) {
            if (distances[i] != 0) {
                int sign = distances[i] > 0 ? 1 : -1;
                carS[i].setX(carS[i].getX() + sign * Property.SPEED);
                distances[i] -= sign * Property.SPEED;
            }
        }
        // Real
        final String status =game.gamePlay.getStatus();
        if (Objects.equals(status, Status.CLIENT_PLAYERS_STATUS)) {
            final ArrayList<Player> players = game.gamePlay.getPlayers();
            for (int i = 0; i < playerCount; ++i) {
                if (players.get(i).isEliminated()) {
                    carS[i].setTexture(carGreyT);
                }
                else {
                    int score = players.get(i).score();
                    if (score > 0) {
                        distances[i] = score * Property.TSIZE - carS[i].getX();
                    }
                    scores[i] = score;
                }
            }
            game.gamePlay.setStatus(null);
        }
        else if (Objects.equals(status, Status.CLIENT_ALL_PLAYERS_ELIMINATED) ||
                Objects.equals(status, Status.CLIENT_END_GAME)) {
            game.gamePlay.setStatus(null);
            game.setScreen(new FinishedScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        viewport.update(width, height);
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
        font.dispose();
        arrowT.dispose();
    }
}
