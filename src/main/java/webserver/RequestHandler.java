package webserver;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String BASE_PATH = "./src/main/resources/static";
    private Socket connection;
    private HttpResponse httpResponse = new HttpResponse();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
//        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
//                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            HttpRequest httpRequest = new HttpRequest(in);

            logger.debug("Request : {}", httpRequest.getRequestLine());
            // 이 아래로 요청에 대한 처리.
            if (httpRequest.getPath().equals("/user/create")) {
                addNewUser(dos, httpRequest);
                return ;
            }

            sendFile(dos, httpRequest);

        } catch (IOException | IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }

    private void sendFile(DataOutputStream dos, HttpRequest request) throws IOException {
        httpResponse.setResponseLine(request.getHttpVersion(), 200);
        ContentType contentType = getContentTypeByPath(request.getPath());
        httpResponse.addHeader("Content-Type", contentType.getContentTypeMsg() + ";charset=utf-8");
        byte[] body = readFileByte(BASE_PATH + request.getPath());
        httpResponse.setBody(body);
        httpResponse.sendResponse(dos);
    }

    // 파일의 경로를 매개로 받아 해당 파일의 내용을 반환
    private byte[] readFileByte(String path) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(path))) {
            byte[] bytes = fis.readAllBytes();
            return bytes;
        }
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

    private void addNewUser(DataOutputStream dos, HttpRequest request) throws IOException {
        HashMap<String, String> dataMap = parseDataString(request.getDataString());
        Database.addUser(new User(dataMap.get("userid"), dataMap.get("password"), dataMap.get("name"), dataMap.get("email")));
        logger.debug("새로운 회원 등록 userID : " + dataMap.get("userid"));

        // 데이터가 포함되어있는 url을 브라우저의 주소창에서 제거하기 위함입니다.
        // 302가 아니라 307을 선택한 이유는, 아직은 굳이 클라이언트의 요청메소드를 바꾸지 않는게 좋을 것 같기 때문입니다.
        httpResponse.setResponseLine(request.getHttpVersion(),  307);
        httpResponse.addHeader("Location", "/index.html");
        httpResponse.sendResponse(dos);
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
