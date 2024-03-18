package Parser;

import webserver.RequestLine;

public class RequestLineParser {

    public RequestLine parse(String requestLine) {
        String[] requestLineParts = requestLine.split(" ");
        String method = requestLineParts[0];
        String path = parsePath(requestLineParts[1]);
        String dataString = parseDataString(requestLineParts[1]);
        String version = requestLineParts[2];
        return new RequestLine(method, path, dataString, version);
    }

    private String parsePath(String path) {
        if (path.contains("?")) {
            return path.substring(0, path.indexOf("?"));
        }
        return path;
    }

    private String parseDataString(String path) {
        if (path.contains("?"))
            return path.substring(path.indexOf("?") + 1);
        return "";
    }
}
