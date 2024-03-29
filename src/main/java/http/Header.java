package http;

import java.util.HashMap;
import java.util.Map;

public class Header {
    // SET-COOKIES
    private static final String SET_COOKIE = "Set-Cookie";
    private final Map<String, String> headerMap;

    public Header() {
        this.headerMap = new HashMap<>();
    }

    public Header(Map<String, String> headerContent) {
        this.headerMap = headerContent;
    }

    public void addHeader(String name, String value) {
        headerMap.put(name, value);
    }

    public void addCookie(String name, String value) {
        if (!headerMap.containsKey(SET_COOKIE)) {
            headerMap.put(SET_COOKIE, "");
        }
        String newCookie = headerMap.get(SET_COOKIE).concat(name + "=" + value + "; ");
        headerMap.put(SET_COOKIE, newCookie);
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public String get(String name) {
        return headerMap.get(name);
    }
}
