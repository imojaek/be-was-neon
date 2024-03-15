package webserver;

public class ResponseLine {
    private String version = "";
    private StatusCode statusCode;

    public ResponseLine(String version, int statusCodeNum) {
        this.version = version;
        setStatusCode(statusCodeNum);
    }

    public ResponseLine(String version, StatusCode statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    private void setStatusCode(int statusCodeNum) {
        for (StatusCode value : StatusCode.values()) {
            if (value.isEqualTo(statusCodeNum))
                this.statusCode = value;
        }
    }

    @Override
    public String toString() {
        return version + " " + statusCode.toString() + " \r\n";
    }
}
