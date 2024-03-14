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

    public HttpRequest(InputStream request) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(request));
        requestLine = RequestLineParser.parse(br.readLine());
        header = makeHeader(br);
        body = makeBody(br);
    }

    private String makeBody(BufferedReader br) throws IOException {
        if (header.containsKey("Content-Length")) {
            int length = Integer.parseInt(header.get("Content-Length"));
            char[] bodyChars = new char[length];
            br.read(bodyChars);
            return new String(bodyChars);
        }
        return "";
    }

    private Map<String, String> makeHeader(BufferedReader br) {
        List<String> header = readHttpHeader(br);
        return parseHeader(header);
    }

    private List<String> readHttpHeader(BufferedReader br) {
        List<String> result = new ArrayList<>();
        String line;
        try {
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                result.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private Map<String, String> parseHeader(List<String> header) {
        Map<String, String> headerMap = new HashMap<>();
        for (String headerInfo : header) {
            String[] split = headerInfo.split(":", 2);
            headerMap.put(split[0], split[1].trim());
        }
        return headerMap;
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
