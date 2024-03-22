package http;

public enum StatusCode {
    OK(200, "OK"),
    FOUND(302, "Found"),
    TEMP_REDIRECT(307, "Temporary Redirect"),
    NOT_FOUND(404, "Not Found");
    private int statusCode;
    private String statusMsg;

    StatusCode(int statusCode, String statusMsg) {
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }

    public boolean isEqualTo(int statusCode) {
        return this.statusCode == statusCode;
    }

    @Override
    public String toString() {
        return statusCode + " " + statusMsg;
    }
}
