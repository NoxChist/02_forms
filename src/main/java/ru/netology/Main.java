package ru.netology;

import ru.netology.server.Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {
    private static final int DEFAULT_PORT = 8888;
    private static final int DEFAULT_POOL_CAPACITY = 64;

    public static void main(String[] args) throws IOException {
        Server server = new Server(DEFAULT_PORT, DEFAULT_POOL_CAPACITY);
        server.addHandler("GET", "/classic.html", ((request, responseStream) -> {
            final Path filePath = Path.of(".", "public", request.getPath());
            final String mimeType = Files.probeContentType(filePath);
            final String template = Files.readString(filePath);
            final String content = template.replace("{time}", LocalDateTime.now().toString());
            responseStream.write(("HTTP/1.1 200 OK\r\n" + "Content-Type: " + mimeType + "\r\n" + "Content-Length: " + content.length() + "\r\n" + "Connection: close\r\n" + "\r\n").getBytes());
            responseStream.write(content.getBytes());
            responseStream.flush();
        }));
        server.addHandler("POST", "/forms.html", ((request, responseStream) -> {
            responseStream.write(("HTTP/1.1 200 OK\r\n" + "Connection: close\r\n" + "\r\n").getBytes());
            responseStream.flush();
        }));
        server.addHandler("POST", "/", ((request, responseStream) -> {
            responseStream.write(("HTTP/1.1 200 OK\r\n" + "Connection: close\r\n" + "\r\n").getBytes());
            responseStream.flush();
        }));
        server.start();
    }
}


