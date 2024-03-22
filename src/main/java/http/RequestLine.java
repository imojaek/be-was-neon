package http;

public class RequestLine {
    private final String method;
    private final String path;
    private final String dataString;
    private final String version;

    public RequestLine(String method, String url, String data, String version) {
        this.method = method;
        this.path = url;
        this.dataString = data;
        this.version = version;
    }

    public String getMethod() {
        return method;
    }
    public String getPath() {
        return path;
    }

    public String getDataString() {
        return dataString;
    }
    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(method).append(" ").append(path);
        if (!dataString.isEmpty()) {
            sb.append("?").append(dataString);
        }
        sb.append(" ").append(version);
        return sb.toString();
    }
}
