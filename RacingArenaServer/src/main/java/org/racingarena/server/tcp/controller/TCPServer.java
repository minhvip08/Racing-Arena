package org.racingarena.server.tcp.controller;

import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.racingarena.server.tcp.ConstantVariable;
import org.racingarena.server.tcp.Status;
import org.racingarena.server.tcp.controller.GamePlay;
import org.racingarena.server.tcp.model.Player;
import org.racingarena.server.tcp.model.WaitingRoom;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

public class TCPServer {
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;
    private final Logger logger;
    private final WaitingRoom waitingRoom;
    private final ServerSocket serverSocket;
    private final GamePlay gamePlay;

    public TCPServer(Logger logger) throws IOException {
        this.logger = logger;

        this.selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocket = serverSocketChannel.socket();
        this.serverSocket.bind(new InetSocketAddress("0.0.0.0", ConstantVariable.PORT));

        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // RETURN SELECTION KEY

        this.waitingRoom = new WaitingRoom(logger);
        this.gamePlay = new GamePlay(logger, waitingRoom);
    }

    public void run() {
        this.logger.info("Server started on port " + ConstantVariable.PORT);
        int numberOfPlayers = 0;

        this.gamePlay.start();
        Iterator<SelectionKey> keys;
        while (true) {
            try {
                if (selector.select() <= 0) {
                    continue;
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                keys = selectedKeys.iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isAcceptable()) {
                        handleAcceptable(key);
                    }
                    if (key.isReadable()) {
                        handleReadable(key);
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


    }

//    Handle client connection to server
    public void handleAcceptable(SelectionKey key) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        SelectionKey selectionKey = client.register(selector, SelectionKey.OP_READ);
        logger.info("Client connected: " + client.getRemoteAddress());
//      Add player's selection key to waiting room
        Player player = new Player(selectionKey,"");
        this.waitingRoom.addPlayer(player);
    }

//    Handle client message to server
    public void handleReadable(SelectionKey key) throws Exception {
        String message = this.waitingRoom.getPlayers().get(key).readTheBuffer();
        if (message == null) {
            JSONObject obj = new JSONObject();
            obj.put("status", Status.CLIENT_ERROR);
            obj.put("message", "Invalid message");
            this.waitingRoom.getPlayers().get(key).writeTheBuffer(obj.toString());
        } else {
            logger.info("Message received: " + message);
            JSONObject obj = new JSONObject(message);
            if (obj.getString("status").equals(Status.SERVER_REGISTER)) {
                handleRegister(key, obj);
            }

            if (obj.getString("status").equals(Status.SERVER_SHUTDOWN)) {
                handleClose(key);
            }
            if (obj.getString("status").equals(Status.SERVER_ANSWER)) {
                logger.info("Message received: " + obj.getString("message"));
                handleAnswer(key, obj);
            }
        }

    }

//    Handle client registration to server
    public void handleRegister(SelectionKey key, JSONObject reqJSON) throws Exception {

        if (reqJSON.getString("status").equals(Status.SERVER_REGISTER)) {
            if (this.waitingRoom.isPlayerRegistered(reqJSON.getString("username"))) {
                this.logger.info("Username already exists");
                JSONObject obj = new JSONObject();
                obj.put("status", Status.CLIENT_REGISTER_AGAIN);
                obj.put("message", "Username already exists");
                sendResponse(key, obj);
                return;
            }
            else if (!reqJSON.getString("username").matches("[a-zA-Z0-9_]{1,10}")){
                this.logger.info("Invalid username");
                JSONObject obj = new JSONObject();
                obj.put("status", Status.CLIENT_REGISTER_AGAIN);
                obj.put("message", "Invalid username");
                sendResponse(key, obj);
                return;
            }

//            if (this.waitingRoom.isFull()) {
//                this.logger.info("Room is full");
//                return;
//            }


        synchronized (this.waitingRoom) {
//            Set name for player
            this.waitingRoom.getPlayers().get(key).setName(reqJSON.getString("username"));
            this.waitingRoom.getPlayerByName(reqJSON.getString("username")).setRegistered(true);
            this.logger.info("Player " + reqJSON.getString("username") + " registered");

//            Send response to client
            JSONObject obj = new JSONObject();
            obj.put("status", Status.CLIENT_REGISTER);
            obj.put("message", "Welcome " + reqJSON.getString("username"));
            obj.put("index", this.waitingRoom.getPlayerByName(reqJSON.getString("username")).index);
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.wrap(obj.toString().getBytes());
            client.write(buffer);
        }

        }
    }
    public void sendResponse(SelectionKey key, JSONObject obj) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.wrap(obj.toString().getBytes());
        client.write(buffer);
    }

//    Handle client close connection to server
    public void handleClose(SelectionKey key) throws IOException {
        JSONObject obj = new JSONObject();
        obj.put("status", Status.CLIENT_SHUTDOWN);
        obj.put("message", "Client " + key.toString() + " closed connection");
        this.waitingRoom.getPlayers().get(key).writeTheBuffer(obj.toString());
        this.waitingRoom.getPlayers().get(key).closeTheClient();
        this.waitingRoom.removePlayer(this.waitingRoom.getPlayers().get(key));
        this.logger.info("Client closed");
    }

//    Handle client answer to server
    public void handleAnswer(SelectionKey key, JSONObject reqJSON) throws IOException {
        int answer = reqJSON.getInt("answer");
        this.waitingRoom.getPlayers().get(key).setAnswer(answer);
        this.waitingRoom.getPlayers().get(key).setTimestamp(Instant.now());
        this.logger.info("Player " + this.waitingRoom.getPlayers().get(key).getName() + " answers: " + answer);
    }


}
