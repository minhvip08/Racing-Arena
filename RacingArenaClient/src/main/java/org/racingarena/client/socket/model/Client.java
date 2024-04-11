package org.racingarena.client.socket.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.racingarena.client.socket.ConstantVariable;
import org.racingarena.client.socket.GamePlay;
import org.racingarena.client.socket.Player;
import org.racingarena.client.socket.Status;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;



public class Client implements Runnable {
    private final SocketChannel client;
    private final Logger logger;
    private final GamePlay gamePlay;

    public Client(Logger logger, GamePlay gamePlay) throws IOException {
        this.logger = logger;
        this.gamePlay = gamePlay;
        client = SocketChannel.open();
        client.configureBlocking(false);
        client.connect(new InetSocketAddress(InetAddress.getByName("localhost"), ConstantVariable.PORT));
        this.logger.info("Client connected to server on port " + ConstantVariable.PORT);
    }

//    This function is used to demonstrate the functionality of the client
    @Override
    public void run() {
        try {
            Selector selector = Selector.open();
            int operations = SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE;
            client.register(selector, operations);
            while (true) {
                if (selector.select() > 0) {
                    boolean doneStatus = processReadySet(selector.selectedKeys());
                    if (gamePlay.isSubmit()){
                        handleAnswer(gamePlay.getQuestionNAnswer().getAnswer());
                        gamePlay.setSubmit(false);
                    }
                    if (doneStatus) {
                        break;
                    }
                }
            }
        } catch (Exception e) {

        }
    }



    private boolean processReadySet (Set readySet) throws Exception {
        Iterator iterator = readySet.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = (SelectionKey) iterator.next();
            iterator.remove();
            if (key.isConnectable()) {
                boolean connected = processConnect(key);
                if (!connected) {
                    return true; // Exit
                }
            }
            if (key.isReadable()) {
                JSONObject msg = processRead(key);
                System.out.println("[Server]: " + msg);
                if (msg == null) {
                    this.handleClose();
                    return true;
                }
                if (!gamePlay.getRegistered()) {
                    switch (msg.getString("status")) {
                        case Status.CLIENT_REGISTER:
                            gamePlay.setRegistered(true);
                            gamePlay.setIndex(msg.getInt("index"));
                            gamePlay.barrier.reset();
                            break;
                        case Status.CLIENT_REGISTER_AGAIN:
                            System.out.println("Username already exists. Enter username: (bye -> exit)");
                            gamePlay.setValidName(false);
                            gamePlay.barrier.reset();
                            break;
                    }
                    continue;
                }
                switch (msg.getString("status")) {
                    case Status.CLIENT_READY:
                        System.out.println("Game is ready to start");
                        gamePlay.setRound(msg.getInt("round"));
                        gamePlay.setPlayerCount(msg.getInt("playerCount"));
                        gamePlay.setStatus(Status.CLIENT_READY);
                        break;
                    case Status.CLIENT_QUESTION:
                        gamePlay.getQuestionNAnswer().setQuestion(msg.getString("message"));
                        gamePlay.getQuestionNAnswer().setDuration(msg.getInt("duration"));
                        gamePlay.setNewQuestion(true);
                        break;
                    case Status.CLIENT_INCORRECT:
                        gamePlay.setStatus(Status.CLIENT_INCORRECT);
                        break;
                    case Status.CLIENT_CORRECT:
                        gamePlay.setStatus(Status.CLIENT_CORRECT);
                        break;
                    case Status.CLIENT_PLAYERS_STATUS:
                        try {
                            ArrayList<Player> playerList = parsePlayerJson(msg.getJSONArray("players"));
                            gamePlay.setPlayers(playerList);
                            gamePlay.setStatus(Status.CLIENT_PLAYERS_STATUS);
                        }
                        catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case Status.CLIENT_END_GAME:
                        System.out.println("Game ended");
                        gamePlay.setStatus(Status.CLIENT_END_GAME);
                        break;
                }
            }
            if (key.isWritable()) {
                if (gamePlay.getUsername() != null) {
                    System.out.println("Enter username: (bye -> exit)");
                    this.handleRegister(gamePlay.getUsername());
                    gamePlay.setUsername(null);
                }
            }
        }
        return false; // Not done yet
    }


    private ArrayList<Player> parsePlayerJson(JSONArray playerJson) {
        ArrayList<Player> playerList = new ArrayList<>();
        playerJson.forEach(player -> {
            JSONObject playerObj = (JSONObject) player;
            playerList.add(new Player(
                playerObj.getString("name"),
                playerObj.getInt("score"),
                playerObj.getBoolean("isEliminated")
            ));
        });
        return playerList;
    }



    public static boolean processConnect(SelectionKey key) throws Exception{
        SocketChannel channel = (SocketChannel) key.channel();
        while (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        return true;
    }

    public void handleAnswer(int answer) {
        System.out.println("SENDING ANSWER");
        JSONObject obj = new JSONObject();
        obj.put("status", Status.SERVER_ANSWER);
        obj.put("answer", answer);
        obj.put("message", "Answer received");
        logger.info("Sending answer: " + answer);
        this.send(obj);
    }

    public boolean handleRegister(String username) {
        JSONObject obj = new JSONObject();
        obj.put("status", Status.SERVER_REGISTER);
        obj.put("username", username);
        this.send(obj);
        return true;
    }

    public JSONObject sendRequest(JSONObject obj) {
        this.send(obj);
        return this.receive();
    }
    public void send(JSONObject obj) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.clear();
            buffer.put(obj.toString().getBytes());
            buffer.flip();
            client.write(buffer);
        } catch (IOException e) {
            logger.severe("Error: " + e.getMessage());
        }
    }

    public JSONObject receive() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.clear();
            client.read(buffer);
            buffer.flip();
            String message = new String(buffer.array()).trim();
            return new JSONObject(message);
        } catch (IOException e) {
            logger.severe("Error: " + e.getMessage());
        }
        return null;
    }
    public void handleClose() throws IOException {
        logger.info("Client closed");
        JSONObject obj = new JSONObject();
        obj.put("status", Status.SERVER_SHUTDOWN);
        this.client.close();
    }

    public JSONObject processRead(SelectionKey key) throws Exception {
        SocketChannel sChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesCount = sChannel.read(buffer);
        if (bytesCount > 0) {
            buffer.flip();
            String message = new String(buffer.array()).trim();
            return new JSONObject(message);
        }
        return null;
    }

}
