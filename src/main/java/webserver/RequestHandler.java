package webserver;

import java.io.*;
import java.io.InputStreamReader;
import java.net.Socket;

import Parser.RequestLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String BASE_PATH = "./src/main/resources/static";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            String requestPath = RequestLineParser.parsePath(readRequestLine(in));
            ContentType contentType = getContentTypeByPath(requestPath);
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = getHtml(BASE_PATH + requestPath).getBytes(); // 여기가 Response Body.
            response200Header(dos, body.length, contentType);
            responseBody(dos, body);
        } catch (IOException | IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }

    // request 의 시작 라인만 읽어 콘솔에 출력해 무엇을 요청하는지 확인할 수 있다.
    private String readRequestLine(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = br.readLine();
        logger.debug("Request : {}", line);
        return line;
    }

    // br의 끝까지 읽으면서 br의 내용을 출력한다.
    private void printRequestAll(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        line = br.readLine();
        logger.debug("Request : {}", line);
        while ((line = br.readLine()) != null && (line.length() > 1)) {
            logger.debug("Header : {}", line);
        }
    }

    // 파일의 경로를 매개로 받아 해당 파일의 내용을 반환
    private String getHtml(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new IOException("file not found : " + path);
        }
        return sb.toString();
    }

    public ContentType getContentTypeByPath(String path) {
        String ext = path.substring(path.lastIndexOf(".") + 1).toUpperCase();
        for (ContentType type : ContentType.values()) {
            if (type.name().equals(ext)) {
                return ContentType.valueOf(ext);
            }
        }
        throw new IllegalArgumentException("Invalid file extension. : " + path); // 없는 확장자인 경우.
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, ContentType contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+ contentType.getContentTypeMsg() + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
