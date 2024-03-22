package http;

import java.util.HashMap;
import java.util.Map;

public class Header {
    private final Map<String, String> headers;

    public Header() {
        this.headers = new HashMap<>();
    }

    public Header(Map<String, String> headerContent) {
        this.headers = headerContent;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String get(String name) {
        return headers.get(name);
    }
}
