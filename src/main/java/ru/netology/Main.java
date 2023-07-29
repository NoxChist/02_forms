package ru.netology;

import ru.netology.server.Server;

import java.io.IOException;

public class Main {
    private static final int DEFAULT_PORT = 9999;
    private static final int DEFAULT_POOL_CAPACITY = 64;

    public static void main(String[] args) throws IOException {
        Server server = new Server(DEFAULT_PORT, DEFAULT_POOL_CAPACITY);
        server.start();
    }
}


