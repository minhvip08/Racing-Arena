package org.racingarena.server.tcp.controller;

import org.json.JSONObject;
import org.racingarena.server.tcp.Status;
import org.racingarena.server.tcp.model.Player;
import org.racingarena.server.tcp.model.Question;
import org.racingarena.server.tcp.model.WaitingRoom;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GamePlay extends Thread {
    private final int MAX_ROUND = 5;
    private final int MAX_FALIED = 3;
    private final int MAX_TIME = 30;
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
            response.put("round", MAX_ROUND);
            response.put("playerCount", waitingRoom.getPlayerRegistered().size());
            waitingRoom.broadcastRegisteredNotEliminatedPlayer(response.toString());
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
            sendPlayersStatus();
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
        List<JSONObject> players = getPlayers();
        response.put("players", players);
        waitingRoom.broadcastRegisteredPlayer(response.toString());
        return question;
    }

    public void checkAnswer(Question question) {
        int incorrect = 0;

        for (Player player : waitingRoom.getRegisteredPlayers()) {

            JSONObject response = new JSONObject();
            if (player.getAnswer() == null || player.getAnswer() != question.getAnswer()) {
                if (!player.isEliminated()){
                    incorrect++;
                    player.setScore(player.getScore() - 1);
                    player.incrementLosingStreak();
                    player.checkLosingStreak();
                }
                response.put("status", Status.CLIENT_INCORRECT);
                response.put("message", "Incorrect answer");
                response.put("isEliminated", player.isEliminated());
                player.writeTheBuffer(response.toString());
            } else {
                if (!player.isEliminated()) {
                    player.setScore(player.getScore() + 1);
                    player.resetLosingStreak();
                }
                response.put("status", Status.CLIENT_CORRECT);
                response.put("message", "Correct answer");
                response.put("isEliminated", player.isEliminated());
                player.writeTheBuffer(response.toString());
            }



        }
        //            Bonus point for fastest player
        Player fastestPlayer = waitingRoom.getFastestPlayer();
        if (fastestPlayer!= null && fastestPlayer.getAnswer() != null && fastestPlayer.getAnswer() == question.getAnswer()){
            fastestPlayer.setScore(fastestPlayer.getScore() + incorrect);
        }

    }

    public void resetRound() {
        for (Player player : waitingRoom.getRegisteredPlayers()) {
            player.resetRound();
        }
    }

    public void resetGame() {
        for (Player player : waitingRoom.getRegisteredPlayers()) {
            player.reset();
//            JSONObject response = new JSONObject();
//            response.put("status", Status.CLIENT_RESET);
//            response.put("message", "Game is reset");
//            player.writeTheBuffer(response.toString());
        }
    }

    public void endGame() {
        JSONObject response = new JSONObject();
        response.put("status", Status.CLIENT_END_GAME);
        response.put("message", "Game is ended");
        response.put("winner", waitingRoom.getHighestScorePlayer().getName());

        waitingRoom.broadcastEndGame(response);
        resetGame();
    }

    private List<JSONObject> getPlayers() {
        List<JSONObject> players = new ArrayList<>();
        for (Player player : waitingRoom.getRegisteredPlayers()) {
            JSONObject playerObj = new JSONObject();
            playerObj.put("name", player.getName());
            playerObj.put("score", player.getScore());
            playerObj.put("isEliminated", player.isEliminated());
            players.add(playerObj);
        }
        return players;
    }

    public void sendPlayersStatus(){
        JSONObject response = new JSONObject();
        response.put("status", Status.CLIENT_PLAYER_STATUS);
        response.put("message", "Players status");
//        JSONObject players = new JSONObject();
        List<JSONObject> players = getPlayers();
        response.put("players", players);
        waitingRoom.broadcastRegisteredPlayer(response.toString());

    }
}
