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
    private List<NameValuePair> queryParams;
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

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public List<NameValuePair> getQueryParams(String name) {
        if (queryParams != null) {
            return queryParams.stream().filter(pair -> name.equalsIgnoreCase(pair.getName())).collect(Collectors.toList());
        }
        return null;
    }

    public static Request requestBuilder(List<String> requestMsg) {
        Request request = null;
        String[] rLineParts = requestMsg.get(0).split(" ");
        if (rLineParts.length == AMOUNT_REQLINE_PARTS) {
            request = new Request();
            request.method = rLineParts[0];
            request.path = rLineParts[1];
            request.queryParams = null;
            if (request.path.contains("?")) {
                final var pathParts = request.path.split("\\?");
                request.path = pathParts[0];
                request.queryParams = URLEncodedUtils.parse(pathParts[1], StandardCharsets.UTF_8);
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
}
