package ru.netology.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int DEFAULT_PORT = 9999;
    private final int DEFAULT_POOL_CAPACITY = 64;
    private ServerSocket server;
    private ExecutorService threadPool;

    public Server() throws IOException {
        server = new ServerSocket(DEFAULT_PORT);
        threadPool = Executors.newFixedThreadPool(DEFAULT_POOL_CAPACITY);
        System.out.println("Server stared on port: " + DEFAULT_PORT);
        System.out.println("Waiting for a connection.");
    }

    public void start() {
        try {
            while (!server.isClosed()) {
                try {
                    Socket clientSocket = server.accept();
                    ClientChannel client = new ClientChannel(clientSocket);
                    threadPool.submit(client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
