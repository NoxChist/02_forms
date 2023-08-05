package ru.netology.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static List<String> pathsList = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    Set<String> validPaths;
    private ServerSocket server;
    private ExecutorService threadPool;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, RequestHandler>> handlers;
    private ConcurrentHashMap<String, RequestHandler> defaultMethodHandlers;

    public Server(int port, int threadPoolCapacity) throws IOException {
        this(port, threadPoolCapacity, new ConcurrentHashMap<>());
    }

    public Server(int port, int threadPoolCapacity, ConcurrentHashMap<String, ConcurrentHashMap<String, RequestHandler>> handlers) throws IOException {
        server = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(threadPoolCapacity);
        this.handlers = handlers;
        validPaths = ConcurrentHashMap.newKeySet();
        validPaths.addAll(pathsList);
        defaultMethodHandlers = new ConcurrentHashMap<>();
        defaultMethodHandlers.put("GET", (request, out) -> {
            final Path filePath = Path.of(".", "public", request.getPath());
            final String mimeType = Files.probeContentType(filePath);
            final long length = Files.size(filePath);

            out.write(("HTTP/1.1 200 OK\r\n" + "Content-Type: " + mimeType + "\r\n" + "Content-Length: " + length + "\r\n" + "Connection: close\r\n" + "\r\n").getBytes());
            Files.copy(filePath, out);
            out.flush();
        });
    }

    public void start() {
        try {
            System.out.println("Server stared on port: " + server.getLocalPort());
            System.out.println("Waiting for a connection.");
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

    public boolean addHandler(String method, String path, RequestHandler handler) {
        if (handlers.containsKey(method)) {
            var methodHandlersMap = handlers.get(method);
            if (validPaths.contains(path)) {
                methodHandlersMap.put(path, handler);
                return true;
            }
        } else {
            if (validPaths.contains(path)) {
                var newPathHandler = new ConcurrentHashMap<String, RequestHandler>();
                newPathHandler.put(path, handler);
                handlers.put(method, newPathHandler);
                return true;
            }
        }
        return false;
    }

    protected class ClientChannel extends Thread {
        private Socket socket;
        private BufferedReader in;
        private BufferedOutputStream out;

        protected ClientChannel(Socket socket) {
            this.socket = socket;
            System.out.println("New client request");
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
                System.out.println("Client channel run");
                List<String> reqMsg = new ArrayList<>();
                String line;
                do {
                    line = in.readLine();
                    reqMsg.add(line);
                } while (in.ready());

                Request request = Request.requestBuilder(reqMsg);
                var t = request.getQueryParams();
                /////////////////////////////////////////////////
                if (request == null) {
                    out.write(("HTTP/1.1 400 Bad Request\r\n" + "Content-Length: 0\r\n" + "Connection: close\r\n" + "\r\n").getBytes());
                    out.flush();
                } else {
                    final String path = request.getPath();

                    if (!validPaths.contains(path)) {
                        out.write(("HTTP/1.1 404 Not Found\r\n" + "Content-Length: 0\r\n" + "Connection: close\r\n" + "\r\n").getBytes());
                        out.flush();
                    } else {
                        if (handlers.containsKey(request.getMethod())) {
                            var PathHandlerMap = handlers.get(request.getMethod());
                            if (PathHandlerMap.containsKey(request.getPath())) {
                                PathHandlerMap.get(request.getPath()).handle(request, out);
                            } else if (defaultMethodHandlers.containsKey(request.getMethod())) {
                                defaultMethodHandlers.get(request.getMethod()).handle(request, out);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
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
}
