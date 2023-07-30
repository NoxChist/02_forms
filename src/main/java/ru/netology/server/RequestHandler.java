package ru.netology.server;

import java.io.BufferedOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface RequestHandler {
    void handle(Request request, BufferedOutputStream responseStream) throws IOException;
}
