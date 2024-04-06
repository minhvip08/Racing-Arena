package org.racingarena.server.tcp;

import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public class TCPServer extends Thread{
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private ServerSocket serverSocket;
    private Logger logger;

    public TCPServer(Logger logger) throws IOException {
        this.logger = logger;

        this.selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocket = serverSocketChannel.socket();
        this.serverSocket.bind(new InetSocketAddress("0.0.0.0", ConstantVariable.PORT));

        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // RETURN SELECTION KEY
    }

    @Override
    public void run() {
        this.logger.info("Server started on port " + ConstantVariable.PORT);
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
                        SocketChannel client = serverSocketChannel.accept();
                        client.configureBlocking(false);

                        client.register(selector, SelectionKey.OP_READ);
                    }
                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        String message = handleRead(key);
                        if (message.equals("NoMessage")) {
                            client.close();
                        } else {
                            logger.info("Message received: " + message);
                            JSONObject obj = new JSONObject(message);
                            if (obj.getString("status").equals(Status.SERVER_REGISTER)){
                                handleRegister(key, obj);

                            }

                            if (obj.getString("status").equals(Status.SERVER_SHUTDOWN)){
                                handleClose(key);
                            }

//                            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
//                            client.write(buffer);

                        }

                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


    }

    public  String handleRead(SelectionKey key) throws Exception {
        SocketChannel sChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesCount = sChannel.read(buffer);
        if (bytesCount > 0) {
            buffer.flip();
            return new String(buffer.array());
        }
        return "NoMessage";
    }

    public void handleRegister(SelectionKey key, JSONObject reqJSON) throws Exception {

        if (reqJSON.getString("status").equals(Status.SERVER_REGISTER)) {
            logger.info("Client registered: " + reqJSON.getString("username"));
            JSONObject obj = new JSONObject();
            obj.put("status", Status.SERVER_REGISTER);
            obj.put("message", "Welcome " + reqJSON.getString("username"));
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.wrap(obj.toString().getBytes());
            client.write(buffer);
        }
    }

    public void handleClose(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        client.close();
    }




}
