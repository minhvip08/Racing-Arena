package org.racingarena.client.socket;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.logging.Logger;

public class TCPClient extends Thread{
    private SocketChannel client;
    private String username;
    private Logger logger;

    public TCPClient(Logger logger) throws IOException {
        this.logger = logger;

        client = SocketChannel.open();
//        client.configureBlocking(false);
        client.connect(new InetSocketAddress(InetAddress.getByName("localhost"), ConstantVariable.PORT));

        this.logger.info("Client connected to server on port " + ConstantVariable.PORT);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        this.username = scanner.nextLine();
    }



    @Override
    public void run() {

        try {
            JSONObject obj = new JSONObject();
            obj.put("status", Status.SERVER_REGISTER);
            obj.put("username", username);
            this.send(obj);
            handleClose();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


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


}
