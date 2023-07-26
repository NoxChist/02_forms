package ru.netology.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class ClientChannel extends Thread {
    private static final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private Socket socket;
    private BufferedReader in;
    private BufferedOutputStream out;

    public ClientChannel(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                String request = in.readLine();
                String[] requestParts = request.split(" ");

                if (requestParts.length != 3) {
                    out.write(("HTTP/1.1 400 Bad Request\r\n" + "Content-Length: 0\r\n" + "Connection: close\r\n" + "\r\n").getBytes());
                    out.flush();
                    continue;
                }
                final String path = requestParts[1];
                if (!validPaths.contains(path)) {
                    out.write(("HTTP/1.1 404 Not Found\r\n" + "Content-Length: 0\r\n" + "Connection: close\r\n" + "\r\n").getBytes());
                    out.flush();
                    continue;
                }
                final Path filePath = Path.of(".", "public", path);
                final String mimeType = Files.probeContentType(filePath);

                if (path.equals("/classic.html")) {
                    final String template = Files.readString(filePath);
                    final String content = template.replace("{time}", LocalDateTime.now().toString());
                    out.write(("HTTP/1.1 200 OK\r\n" + "Content-Type: " + mimeType + "\r\n" + "Content-Length: " + content.length() + "\r\n" + "Connection: close\r\n" + "\r\n").getBytes());
                    out.write(content.getBytes());
                    out.flush();
                    continue;
                }

                final long length = Files.size(filePath);
                out.write(("HTTP/1.1 200 OK\r\n" + "Content-Type: " + mimeType + "\r\n" + "Content-Length: " + length + "\r\n" + "Connection: close\r\n" + "\r\n").getBytes());
                Files.copy(filePath, out);
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
