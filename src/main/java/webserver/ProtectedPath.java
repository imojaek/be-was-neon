package webserver;

public enum ProtectedPath {
    USER_LIST("/user/list");

    private final String path;

    ProtectedPath(String path) {
        this.path = path;
    }
    public String getPath() {
        return path;
    }
}
