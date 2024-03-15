package webserver;

import Parser.RequestLineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private RequestLine requestLine;
    private Map<String, String> header;
    private String body;

    public HttpRequest(RequestLine requestLine, Map<String, String> header, String body) {
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
        return requestLine.toString() + "\n" + header + "\n" + body;
    }
}
