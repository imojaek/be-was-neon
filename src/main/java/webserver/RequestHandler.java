package webserver;

import java.io.*;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

import Parser.RequestLineParser;
import db.Database;
import model.User;
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
            DataOutputStream dos = new DataOutputStream(out);
            RequestLine requestLine = RequestLineParser.parse(readRequestLine(in));

            if (requestLine.getPath().equals("/user/create")) {
                addNewUser(dos, requestLine.getDataString());
                return ;
            }

            ContentType contentType = getContentTypeByPath(requestLine.getPath());
            byte[] body = getHtml(BASE_PATH + requestLine.getPath()).getBytes();
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
        throw new IllegalArgumentException("Invalid file extension. : " + path); //  지원하지 않는 확장자인 경우.
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

    private void response307Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 307 Temporary Redirect \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
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

    private void addNewUser(DataOutputStream dos, String dataString) throws IOException {
        HashMap<String, String> dataMap = parseDataString(dataString);
        Database.addUser(new User(dataMap.get("userid"), dataMap.get("password"), dataMap.get("name"), dataMap.get("email")));
        logger.debug("새로운 회원 등록 userID : " + dataMap.get("userid"));

        // 데이터가 포함되어있는 url을 브라우저의 주소창에서 제거하기 위함입니다.
        // 302가 아니라 307을 선택한 이유는, 아직은 굳이 클라이언트의 요청메소드를 바꾸지 않는게 좋을 것 같기 때문입니다.
        response307Header(dos, "/index.html");
    }

    private HashMap<String, String> parseDataString(String dataString) {
        HashMap<String, String> dataMap = new HashMap<>();
        String[] datas = dataString.split("&");
        for (String data : datas) {
            String[] splitData = data.split("=");
            dataMap.put(splitData[0], splitData[1]);
        }
        return dataMap;
    }
}
