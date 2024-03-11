package Parser;

public class RequestLineParser {
    public static String parseMethod(String requestLine) {
        String[] requestLineParts = requestLine.split(" ");
        return requestLineParts[0];
    }

    public static String parsePath(String requestLine) {
        String[] requestLineParts = requestLine.split(" ");
        if (requestLineParts[1].equals("/"))
            return "/index.html";
        return requestLineParts[1];
    }

    public static String parseVersion(String requestLine) {
        String[] requestLineParts = requestLine.split(" ");
        return requestLineParts[2];
    }
}
