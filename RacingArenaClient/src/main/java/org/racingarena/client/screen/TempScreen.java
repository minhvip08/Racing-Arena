package org.racingarena.client.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import org.racingarena.client.game.RacingArena;
import org.racingarena.client.object.Calculation;

import java.util.*;

public class TempScreen implements Screen {
    final RacingArena game;
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

    public TempScreen(final RacingArena game) {
        this.game = game;

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
        table.add(answer).width(150);;

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
        rootTable.add(panelTable).maxHeight(150.0f).colspan(2).grow();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        timeSeconds +=Gdx.graphics.getRawDeltaTime();
        if(timeSeconds > period){
            timeSeconds-=period;
            timer--;
            if (timer < 0){
                timer = 10;
                calculation.newCalculation();
                calculationLabel.setText(calculation.getCalculation());
            }
            timerLabel.setText(Integer.toString(timer));
        }
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
