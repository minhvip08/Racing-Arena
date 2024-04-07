package org.racingarena.server.tcp.controller;

import org.json.JSONObject;
import org.racingarena.server.tcp.Status;
import org.racingarena.server.tcp.model.Player;
import org.racingarena.server.tcp.model.Question;
import org.racingarena.server.tcp.model.WaitingRoom;

import java.nio.channels.SelectionKey;
import java.util.logging.Logger;

public class GamePlay extends Thread {
    private final int MAX_ROUND = 2;
    private final int MAX_FALIED = 3;
    private final int MAX_TIME = 2;
    private Logger logger;
    private WaitingRoom waitingRoom;

    public GamePlay(Logger logger, WaitingRoom waitingRoom) {
        this.logger = logger;
        this.waitingRoom = waitingRoom;
    }

    @Override
    public void run() {
        while (true) {
            if (waitingRoom.getPlayerRegistered().size() != WaitingRoom.MAX_PLAYER) {
                continue;
            }
            JSONObject response = new JSONObject();
            response.put("status", Status.CLIENT_READY);
            response.put("message", "Game is ready to start");
            response.put("duration", 10);
            waitingRoom.broadcastRegistered(response.toString());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startGame();
            endGame();
        }
    }

    public void startGame() {
        for (int round = 0; round < MAX_ROUND; round++) {
            Question currentQuestion = startRound();

//            Wait for all players to answer
            try {
                Thread.sleep(MAX_TIME * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            checkAnswer(currentQuestion);
            resetRound();


        }
    }

    //    Send question to all players
    public Question startRound() {
        Question question = new Question();
        JSONObject response = new JSONObject();
        response.put("status", Status.CLIENT_QUESTION);
        response.put("message", question.getQuestion());
        response.put("duration", MAX_TIME);
        waitingRoom.broadcastRegistered(response.toString());
        return question;
    }

    public void checkAnswer(Question question) {
        int incorrect = 0;
        Player fastestPlayer =null;
        for (Player player : waitingRoom.getSortedPlayersByTimestamp()) {
            if (fastestPlayer == null) {
                fastestPlayer = player;
            }
            JSONObject response = new JSONObject();
            if (player.getAnswer() == null || player.getAnswer() != question.getAnswer()) {
                incorrect++;
                player.setScore(player.getScore() - 1);
                response.put("status", Status.CLIENT_INCORRECT);
                response.put("message", "Incorrect answer");
                player.writeTheBuffer(response.toString());
            } else {
                player.setScore(player.getScore() + 1);
                response.put("status", Status.CLIENT_CORRECT);
                response.put("message", "Correct answer");
                player.writeTheBuffer(response.toString());
            }


//            Bonus point for fastest player
            if (fastestPlayer.getAnswer() != null && fastestPlayer.getAnswer() == question.getAnswer()){
                fastestPlayer.setScore(fastestPlayer.getScore() + incorrect);
            }

        }
    }

    public void resetRound() {
        for (Player player : waitingRoom.getReadyPlayers()) {
            player.resetRound();
        }
    }

    public void resetGame() {
        for (Player player : waitingRoom.getReadyPlayers()) {
            player.reset();
        }
    }

    public void endGame() {
        JSONObject response = new JSONObject();
        response.put("status", Status.CLIENT_END_GAME);
        response.put("message", "Game is ended");
        response.put("winner", waitingRoom.getHighestScorePlayer().getName());
        waitingRoom.broadcastRegistered(response.toString());
        resetGame();
    }
}
