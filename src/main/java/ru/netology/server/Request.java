package ru.netology.server;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private final static int AMOUNT_REQLINE_PARTS = 3;
    private String method;
    private String path;
    private String query;
    private String protocol;
    private List<String> body;

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public List<String> getBody() {
        return body;
    }

    public static Request requestBuilder(List<String> requestMsg) {
        Request request = null;
        String[] rLineParts = requestMsg.get(0).split(" ");
        if (rLineParts.length == AMOUNT_REQLINE_PARTS) {
            request = new Request();
            request.method = rLineParts[0];
            request.query = null;
            request.path = rLineParts[1];
            if (request.path.contains("?")) {
                final var pathParts = request.path.split("\\?");
                request.path = pathParts[0];
                request.query = pathParts[1];
            }
            request.protocol = rLineParts[2];
            request.body = null;
            if (!requestMsg.get(requestMsg.size() - 1).equalsIgnoreCase("")) {
                request.body = new ArrayList<>();
                int indexBodySeparator = -1;
                for (int i = 1; i < requestMsg.size(); i++) {
                    if (indexBodySeparator == -1) {
                        if (requestMsg.get(i).equalsIgnoreCase("")) {
                            indexBodySeparator = i;
                        }
                    } else {
                        request.body.add(requestMsg.get(i));
                    }
                }
            }
        }
        return request;
    }

    public List<NameValuePair> getQueryParams() {
        if (query != null) {
            return URLEncodedUtils.parse(query, StandardCharsets.UTF_8);
        }
        return null;
    }

    public List<NameValuePair> getQueryParams(String name) {
        if (query != null) {
            return URLEncodedUtils.parse(query, StandardCharsets.UTF_8).stream().filter(pair -> name.equalsIgnoreCase(pair.getName())).collect(Collectors.toList());
        }
        return null;
    }
}
