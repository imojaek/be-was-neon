package webserver;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class PostMethodHandler {
    private Map<String, BiConsumer<DataOutputStream, HttpRequest>> actionMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(PostMethodHandler.class);
    private final HttpResponse httpResponse = new HttpResponse();

    public PostMethodHandler() {
        makeActionMap();
    }

    public HttpResponse actionByPath(DataOutputStream dos, HttpRequest request) {
        for (String definedPath : actionMap.keySet()) {
            if (definedPath.equals(request.getPath())) {
                actionMap.get(definedPath).accept(dos, request);
                return this.httpResponse;
            }
        }
        logger.error("POST 메시지에 해당하는 메소드가 없습니다.");
        set404ErrorResponse(request);
        return this.httpResponse;
    }

    private void addNewUser(DataOutputStream dos, HttpRequest request) throws IOException {
        HashMap<String, String> dataMap = parseDataString(new String(request.getBody(), "UTF-8"));
        Database.addUser(new User(dataMap.get("userid"), dataMap.get("password"), dataMap.get("name"), dataMap.get("email")));
        logger.debug("새로운 회원 등록 userID : " + dataMap.get("userid"));

        setRedirectReponse(request, "/index.html");
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

    private void setRedirectReponse(HttpRequest request, String path) {
        httpResponse.setResponseLine(request.getHttpVersion(), 302);
        httpResponse.addHeader("Location", path);
    }

    private void set404ErrorResponse(HttpRequest request) {
        httpResponse.setResponseLine(request.getHttpVersion(), 404);
    }
}
