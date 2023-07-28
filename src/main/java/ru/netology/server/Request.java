package ru.netology.server;

import java.util.ArrayList;
import java.util.List;

public class Request {
    private final static int AMOUNT_REQLINE_PARTS = 3;
    private String method;
    private String path;
    private String protocol;
    private List<String> body;

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return method;
    }

    public String getProtocol() {
        return method;
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
            request.path = rLineParts[1];
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
