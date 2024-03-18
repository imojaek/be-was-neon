package webserver;

import Parser.RequestParser;
import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String BASE_PATH = "./src/main/resources/static";
    private final Socket connection;
    private final HttpResponse httpResponse = new HttpResponse();
    private Map<String, BiConsumer<DataOutputStream, HttpRequest>> actionMap = new HashMap<>();
    private final RequestParser requestParser = new RequestParser();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        makeActionMap();
    }

    public void run() {
//        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
//                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            HttpRequest httpRequest = requestParser.parse(in);

            logger.debug("Request : {}", httpRequest.getRequestLine());
            // 이 아래로 요청에 대한 처리.
            actionByPath(dos, httpRequest);

        } catch (IOException | IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }

    private void actionByPath(DataOutputStream dos, HttpRequest request) throws IOException {
        for (String definedPath : actionMap.keySet()) {
            if (definedPath.equals(request.getPath())) {
                actionMap.get(definedPath).accept(dos, request);
                return ;
            }
        }
        sendFile(dos, request);
    }

    private Map<String, BiConsumer<DataOutputStream, HttpRequest>> makeActionMap() {
        actionMap.put("/user/create", (inputDos, inputRequest) -> {
            try {
                addNewUser(inputDos, inputRequest);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });
        return actionMap;
    }

    // sendFile이 실행되는 경우는 Method가 GET이고, Target이 특정한 동작을 요구하지 않는 상황인 경우입니다.
    private void sendFile(DataOutputStream dos, HttpRequest request) throws IOException {
        httpResponse.setResponseLine(request.getHttpVersion(), 200);
        ContentType contentType = getContentTypeByPath(request.getPath()); // 확장자가 없는 폴더의 경우, index.html을 호출할 것이므로 HTML을 반환할 것입니다.
        httpResponse.addHeader("Content-Type", contentType.getContentTypeMsg() + ";charset=utf-8");
        byte[] body = readFileByte(modifyRequsetPath(request));
        httpResponse.setBody(body);
        httpResponse.sendResponse(dos);
    }

    private String modifyRequsetPath(HttpRequest request) {
        String requestPath = request.getPath();
        if (requestPath.endsWith("/")) {
            return BASE_PATH + requestPath + "index.html";
        }
        if (!requestPath.contains(".")) {
            return BASE_PATH + requestPath + "/index.html";
        }
        return BASE_PATH + requestPath;
    }

    // 파일의 경로를 매개로 받아 해당 파일의 내용을 반환
    private byte[] readFileByte(String path) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(path))) {
            byte[] bytes = fis.readAllBytes();
            return bytes;
        }
    }
    public ContentType getContentTypeByPath(String path) {
        // 경로에 .이 없어 index.html 을 참조하게 되는 경우.
        if (!path.contains("."))
            return ContentType.HTML;

        String ext = path.substring(path.lastIndexOf(".") + 1).toUpperCase();
        for (ContentType type : ContentType.values()) {
            if (type.name().equals(ext)) {
                return ContentType.valueOf(ext);
            }
        }
        throw new IllegalArgumentException("Invalid file extension. : " + path); //  지원하지 않는 확장자인 경우.
    }

    private void addNewUser(DataOutputStream dos, HttpRequest request) throws IOException {
        HashMap<String, String> dataMap = parseDataString(new String(request.getBody(), "UTF-8"));
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
