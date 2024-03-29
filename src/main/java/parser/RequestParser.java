package parser;

import http.HttpRequest;
import http.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class RequestParser {
    private final RequestLineParser requestLineParser = new RequestLineParser();
    private final Logger logger = LoggerFactory.getLogger(RequestParser.class);
    private static final int BUFFER_SIZE = 10000;


    public HttpRequest parse(InputStream request) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(request);
        RequestLine requestLine = requestLineParser.parse(readRequestLine(bis));
        Map<String, String> headers = makeHeader2(bis);
        byte[] body = makeBody(bis, headers);

        return new HttpRequest(requestLine, headers, body);
    }

    private byte[] makeBody(BufferedInputStream bis, Map<String, String> headers) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (headers.containsKey("Content-Length") && Integer.parseInt(headers.get("Content-Length")) > 0) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            byte[] buffer = new byte[BUFFER_SIZE];
            int readLength = Math.min(BUFFER_SIZE, contentLength);
            while(contentLength > 0) {
                int read = bis.read(buffer, 0, readLength);
                contentLength -= read;
                bos.write(buffer, 0, read);
            }

            return bos.toByteArray();
        }
        return new byte[0];
    }

    private Map<String, String> makeHeader2(BufferedInputStream bis) throws IOException {
        List<byte[]> headerList = new ArrayList<>();

        byte[] header;

        while ((header = readLine(bis)).length != 0) {
            headerList.add(header);
        }

        List<String> lineList = headerList.stream()
                                        .map(line -> new String(line, StandardCharsets.UTF_8))
                                        .collect(Collectors.toList());

        Map<String, String> resultMap = parseHeader(lineList);

        return resultMap;
    }

    private String readRequestLine(BufferedInputStream bis) throws IOException {
        byte[] line = readLine(bis);
        return new String(line, StandardCharsets.UTF_8);
    }

    private byte[] readLine(BufferedInputStream bis) throws IOException {
        List<Byte> bytes = new ArrayList<>();
        int prevData = -1;
        int data;
        while ((data = bis.read()) != -1) {
            if (prevData == '\r' && data == '\n') {
                bytes.remove(bytes.size() - 1); // bytes에 들어있는 \r 을 제거한다.
                break;
            }
            bytes.add((byte) data);
            prevData = data;
        }
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }

    private Map<String, String> parseHeader(List<String> header) {
        Map<String, String> headerMap = new HashMap<>();
        for (String headerInfo : header) {
            String[] split = headerInfo.split(":", 2);
            headerMap.put(split[0].trim(), split[1].trim());
        }
        return headerMap;
    }
}
