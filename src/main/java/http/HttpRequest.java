package http;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Header headers;
    private final Body body;
    private final Optional<Map<String, String>> cookies;
    private Map<String, String> bodyDataMap = null;

    private static final String ENCODING = "UTF-8";

    public HttpRequest(RequestLine requestLine, Map<String, String> headerContent, byte[] bodyContent) {
        this.requestLine = requestLine;
        this.headers = new Header(headerContent);
        this.body = new Body(bodyContent);
        this.cookies = makeCookies();
    }

    public Map<String, String> getBodyDataMap() {
        if (bodyDataMap == null) {
            String str = new String(getBodyContent(), StandardCharsets.UTF_8);
            bodyDataMap = makeBodyData(str);
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
        String cookieString = headers.get("Cookie");
        Map<String, String> tempCookies = new HashMap<>();
        if (cookieString != null && !cookieString.isEmpty()) {
            String[] cookiePair = cookieString.split(";");
            for (String pair : cookiePair) {
                String[] split = pair.split("=", 2);
                tempCookies.put(split[0].trim(), split[1]);  // 하나의 쿠키에 속성이 여러가지일 경우 세미콜론과 공백으로 구분된다.
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
    public byte[] getBodyContent() {
        return body.getBodyContent();
    }

    public Optional<Map<String, String>> getCookies() {
        return cookies;
    }

    public String getSessionId() {
        return cookies.map(cookie -> cookie.get("sid")).orElse("");
    }

    @Override
    public String toString() {
        try {
            return requestLine.toString() + "\n" + headers.getHeaderMap() + "\n" + new String(body.getBodyContent(), ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
