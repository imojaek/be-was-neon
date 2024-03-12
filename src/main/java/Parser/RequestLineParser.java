package Parser;

import webserver.RequestLine;

public class RequestLineParser {

    public static RequestLine parse(String requestLine) {
        String[] requestLineParts = requestLine.split(" ");
        String method = requestLineParts[0];
        String path = parsePath(requestLineParts[1]);
        String dataString = parseDataString(requestLineParts[1]);
        String version = requestLineParts[2];
        return new RequestLine(method, path, dataString, version);
    }

    private static String parsePath(String path) {
        if (path.contains("?")) {
            return path.substring(0, path.indexOf("?"));
        }
        return parsePathWithIndex(path);
    }

    // 요청받은 path가 파일이 아닌 경우에, path 경로의 index.html 을 반환한다.
    private static String parsePathWithIndex(String path) {
        if (path.endsWith("/"))
            return path + "index.html";
        if (path.contains("."))
            return path;
        return path + "/index.html";
    }

    private static String parseDataString(String path) {
        if (path.contains("?"))
            return path.substring(path.indexOf("?") + 1);
        return "";
    }
}
