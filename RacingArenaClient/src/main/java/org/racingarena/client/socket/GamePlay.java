package org.racingarena.client.socket;
import org.racingarena.client.object.QuestionNAnswer;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class GamePlay {
    private int round;
    private boolean registered;
    private String username;
    private int index;
    private int playerCount;
    private String status;
    private boolean newQuestion = false;
    private QuestionNAnswer questionNAnswer;
    private boolean PrevCorrect = false;
    private boolean submit = false;
    private boolean received = false;
    private Winner winner;

    private boolean validName;
    private ArrayList<Player> players;
    public final CyclicBarrier barrier;
    private boolean running = true;

    public synchronized String getUsername() {
        return username;
    }

    public synchronized void setUsername(String username) {
        this.username = username;
    }

    public synchronized boolean getRegistered() {
        return registered;
    }

    public synchronized void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public synchronized ArrayList<Player> getPlayers() {
        return players;
    }

    public synchronized void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public synchronized void setStatus(String status) {
        this.status = status;
    }

    public synchronized String getStatus() {
        return status;
    }

    public synchronized void setRound(int round) {
        this.round = round;
    }

    public synchronized int getRound() {
        return round;
    }

    public synchronized int getIndex() {
        return index;
    }

    public synchronized void setIndex(int index) {
        this.index = index;
    }
    public QuestionNAnswer getQuestionNAnswer(){return questionNAnswer;}

    public void setQuestionNAnswer(QuestionNAnswer questionNAnswer) {
        this.questionNAnswer = questionNAnswer;
    }

    public void setValidName(boolean validName) {
        this.validName = validName;
    }

    public boolean isValidName() {
        return validName;
    }
    public void setSubmit(boolean submit) {
        this.submit = submit;
    }
    public boolean isSubmit() {
        return submit;
    }
    public boolean isPrevCorrect() {return PrevCorrect;}
    public void setPrevCorrect(boolean prevCorrect) {PrevCorrect = prevCorrect;}
    public boolean isNewQuestion() {
        return newQuestion;
    }
    public void setNewQuestion(boolean newQuestion) {
        this.newQuestion = newQuestion;
    }
    public boolean isReceived() {return received;}
    public void setReceived(boolean received) {this.received = received;}

    public synchronized int getPlayerCount() {
        return this.playerCount;
    }

    public synchronized void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public synchronized void setWinner(Winner winner) {
        this.winner = winner;
    }

    public synchronized Winner getWinner() {
        return this.winner;
    }

    public synchronized void shutdown() {
        this.running = false;
    }

    public synchronized boolean isRunning() {
        return this.running;
    }

    public synchronized void reset() {
        this.username = null;
        this.registered = false;
        this.status = null;
        this.players = null;
        this.round = 0;
        this.winner = null;
        this.playerCount = 0;
        this.validName = true;
        this.questionNAnswer = new QuestionNAnswer();
    }

    public GamePlay() {
        this.barrier = new CyclicBarrier(2);
        this.username = null;
        this.registered = false;
        this.status = null;
        this.players = null;
        this.round = 0;
        this.winner = null;
        this.playerCount = 0;
        this.validName = true;
        this.questionNAnswer = new QuestionNAnswer();
    }
}
