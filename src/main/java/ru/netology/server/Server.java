package ru.netology.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket server;
    private ExecutorService threadPool;

    public Server(int port, int threadPoolCapacity) throws IOException {
        server = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(threadPoolCapacity);
        System.out.println("Server stared on port: " + port);
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
