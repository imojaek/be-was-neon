package webserver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, String> header;
    private final Optional<Map<String, String>> cookies;
    private final byte[] body;
    private Map<String, String> bodyDataMap = null;

    private static final String ENCODING = "UTF-8";

    public HttpRequest(RequestLine requestLine, Map<String, String> header, byte[] body) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
        this.cookies = makeCookies();
    }

    public Map<String, String> getBodyDataMap() {
        if (bodyDataMap == null) {
            bodyDataMap = makeBodyData(new String(getBody(), StandardCharsets.UTF_8));
        }
        return bodyDataMap;
    }

    private HashMap<String, String> makeBodyData(String dataString) {
        HashMap<String, String> dataMap = new HashMap<>();
        String[] datas = dataString.split("&");
        for (String data : datas) {
            String[] splitData = data.split("=");
            dataMap.put(splitData[0], splitData[1]);
        }
        return dataMap;
    }
    private Optional<Map<String, String>> makeCookies() {
        String cookieString = header.get("Cookie");
        Map<String, String> tempCookies = new HashMap<>();
        if (cookieString != null && !cookieString.isEmpty()) {
            String[] cookiePair = cookieString.split(";");
            for (String pair : cookiePair) {
                String[] split = pair.split("=", 2);
                tempCookies.put(split[0], split[1]);
            }
            return Optional.of(tempCookies);
        }
        return Optional.empty();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }
    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getHttpVersion() {
        return requestLine.getVersion();
    }

    public String getPath() {
        return requestLine.getPath();
    }
    public byte[] getBody() {
        return body;
    }

    public Optional<Map<String, String>> getCookies() {
        return cookies;
    }

    @Override
    public String toString() {
        try {
            return requestLine.toString() + "\n" + header + "\n" + new String(body, ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
