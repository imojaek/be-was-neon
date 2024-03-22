package http;

public class Body {
    private final byte[] bodyContent;

    public Body(byte[] bodyContent) {
        this.bodyContent = bodyContent;
    }

    public byte[] getBodyContent() {
        return bodyContent;
    }
}
