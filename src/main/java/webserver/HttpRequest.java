package webserver;

import Parser.RequestLineParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private RequestLine requestLine;
    private Map<String, String> header;
    private byte[] body;

    private final String ENCODING = "UTF-8";

    public HttpRequest(RequestLine requestLine, Map<String, String> header, byte[] body) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getHttpVersion() {
        return requestLine.getVersion();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getDataString() {
        return requestLine.getDataString();
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
