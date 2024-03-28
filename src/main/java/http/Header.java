package http;

import java.util.HashMap;
import java.util.Map;

public class Header {
    // SET-COOKIES
    private static final String SET_COOKIE = "Set-Cookie";
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

    public void addCookie(String name, String value) {
        if (!headers.containsKey(SET_COOKIE)) {
            headers.put(SET_COOKIE, "");
        }
        String newCookie = headers.get(SET_COOKIE).concat(name + "=" + value + "; ");
        headers.put(SET_COOKIE, newCookie);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String get(String name) {
        return headers.get(name);
    }
}
