package webserver;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    SVG("image/svg+xml"),
    ICO("image/x-icon");
    private String contentTypeMsg;
    ContentType(String contentTypeMsg) {
        this.contentTypeMsg = contentTypeMsg;
    }
    public String getContentTypeMsg() {
        return contentTypeMsg;
    }
}
