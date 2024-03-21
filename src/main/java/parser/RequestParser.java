package parser;

import webserver.HttpRequest;
import webserver.RequestLine;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParser {
    private final RequestLineParser requestLineParser = new RequestLineParser();
    private static final int BUFFER_SIZE = 10000;

    public HttpRequest parse(InputStream request) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(request));
        RequestLine requestLine = requestLineParser.parse(br.readLine());
        Map<String, String> headers = makeHeader(br);
        byte[] body = makeBody(br, headers);

        return new HttpRequest(requestLine, headers, body);
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

    private byte[] makeBody(BufferedReader br, Map<String, String> headers) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (headers.containsKey("Content-Length") && Integer.parseInt(headers.get("Content-Length")) > 0) {
            char[] buf = new char[BUFFER_SIZE];
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            while (contentLength > 0) {
                int readlen = Math.min(contentLength, BUFFER_SIZE);
                int len = br.read(buf, 0, readlen);
                sb.append(buf, 0, len);
                contentLength -= len;
            }
            return sb.toString().getBytes();
        }
        return new byte[0];
    }
}
