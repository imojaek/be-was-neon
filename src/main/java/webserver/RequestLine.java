package webserver;

public class RequestLine {
    private String method;
    private String path;
    private String dataString;
    private String version;

    public RequestLine(String method, String url, String data, String version) {
        this.method = method;
        this.path = url;
        this.dataString = data;
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public String getDataString() {
        return dataString;
    }
}
