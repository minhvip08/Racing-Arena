package org.racingarena.client.socket;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

public class TCPClient{
    private SocketChannel client;
    private String username;
    private Logger logger;

    private Selector selector;
    public TCPClient(Logger logger) throws IOException {
        this.logger = logger;

        client = SocketChannel.open();
        client.configureBlocking(false);
        client.connect(new InetSocketAddress(InetAddress.getByName("localhost"), ConstantVariable.PORT));

        this.logger.info("Client connected to server on port " + ConstantVariable.PORT);

        Scanner scanner = new Scanner(System.in);
    }

    public void run() {

        try {
            this.selector = Selector.open();
            int operations = SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE;
            client.register(selector, operations);
            while (true) {
                if (selector.select() > 0) {
                    boolean doneStatus = processReadySet(selector.selectedKeys());
                    if (doneStatus) {
                        break;
                    }
                }
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
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
            }
            if (key.isWritable()) {
                System.out.println("Enter username: (bye -> exit)");
                Scanner scanner = new Scanner(System.in);
                String username = scanner.nextLine();


                if (username.equalsIgnoreCase("bye")) {
                    this.client.close();
                    return true; // Exit
                }

//                JSONObject obj = new JSONObject();
//                obj.put("status", Status.SERVER_MESSAGE);
//                obj.put("message", username);
//                this.send(obj);
                this.handleRegister(username);
            }
        }
        return false; // Not done yet
    }

    public static boolean processConnect(SelectionKey key) throws Exception{
        SocketChannel channel = (SocketChannel) key.channel();
        while (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        return true;
    }

    public boolean handleRegister(String username) {
        this.username = username;
        JSONObject obj = new JSONObject();
        obj.put("status", Status.SERVER_REGISTER);
        obj.put("username", username);
        this.send(obj);
        return true;
//        JSONObject response = this.sendRequest(obj);
//        if (response == null) {
//            return false;
//        }
//        boolean status = response.getBoolean("status");
//        if (status) {
//            logger.info("Registered successfully");
//            return true;
//        } else {
//            logger.info("Username already exists");
//            return false;
//        }
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

    public  JSONObject processRead(SelectionKey key) throws Exception {
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
