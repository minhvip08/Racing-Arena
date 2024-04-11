package org.racingarena.server.tcp.model;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.time.Instant;

public class Player {
    public static final int LOSING_STREAK_LIMIT = 3;
    private SelectionKey key;
    private String name ="";
    private int score;
    private SocketChannel client;
    private boolean isReady;
    private boolean isRegistered;
    private boolean isEliminated;
    private Instant timestamp;
    private Integer answer;
    private Integer losingStreak = 0;

    public Player(SelectionKey key, String name) {
        this.key = key;
        this.name = name;
        this.client = (SocketChannel) key.channel();
    }

    public void checkLosingStreak() {
        if (losingStreak >= LOSING_STREAK_LIMIT) {
            this.isEliminated = true;
        }
    }

    public void incrementLosingStreak() {
        losingStreak++;
    }

    public void resetLosingStreak() {
        losingStreak = 0;
    }

    public String readTheBuffer() {
        if (this.key.isReadable()){
            try {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                this.client.read(buffer);
                if (buffer.array().length == 0) {
                    return null;
                }
                return new String(buffer.array()).trim();
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public Boolean writeTheBuffer(String message) {

        try {
            if (this.client.socket().isClosed()) {
                return false;
            }
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            this.client.write(buffer);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void reset() {
        this.isReady = false;
        this.isRegistered = false;
        this.isEliminated = false;
        this.timestamp = null;
        this.answer = null;
        this.score = 0;
        this.name = "";
    }

    public void resetRound() {
        this.answer = null;
        this.timestamp = null;
    }
    public void closeTheClient() {
        try {
            this.client.close();
        } catch (Exception e) {
            return;
        }
    }

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public SocketChannel getClient() {
        return client;
    }

    public void setClient(SocketChannel client) {
        this.client = client;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public boolean isEliminated() {
        return isEliminated;
    }

    public void setEliminated(boolean eliminated) {
        isEliminated = eliminated;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }
}
