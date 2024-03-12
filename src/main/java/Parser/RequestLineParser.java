package Parser;

public class RequestLineParser {
    public static String parseMethod(String requestLine) {
        String[] requestLineParts = requestLine.split(" ");
        return requestLineParts[0];
    }

    public static String parsePath(String requestLine) {
        String[] requestLineParts = requestLine.split(" ");
        return parsePathWithIndex(requestLineParts[1]);
    }

    public static String parseVersion(String requestLine) {
        String[] requestLineParts = requestLine.split(" ");
        return requestLineParts[2];
    }

    // 요청받은 path가 파일이 아닌 경우에, path 경로의 index.html 을 반환한다.
    public static String parsePathWithIndex(String path) {
        if (path.endsWith("/"))
            return path + "index.html";
        if (path.contains("."))
            return path;
        return path + "/index.html";
    }
}
