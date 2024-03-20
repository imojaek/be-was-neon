package webserver;

import Sessions.Session;
import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class PostMethodHandler {
    private final Map<String, Consumer<HttpRequest>> actionMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(PostMethodHandler.class);
    private final HttpResponse httpResponse = new HttpResponse();
    private HashMap<String, String> dataMap;
    private Session session;


    public PostMethodHandler() {
        makeActionMap();
    }

    public HttpResponse actionByPath(HttpRequest request, Session session) throws UnsupportedEncodingException {
        dataMap = parseDataString(new String(request.getBody(), "UTF-8"));
        this.session = session;
        for (String definedPath : actionMap.keySet()) {
            if (definedPath.equals(request.getPath())) {
                actionMap.get(definedPath).accept(request);
                return this.httpResponse;
            }
        }
        logger.error("POST 메시지에 해당하는 메소드가 없습니다.");
        set404ErrorResponse(request);
        return this.httpResponse;
    }

    private void addNewUser(HttpRequest request) {
        Database.addUser(new User(dataMap.get("userid"), dataMap.get("password"), dataMap.get("name"), dataMap.get("email")));
        logger.debug("새로운 회원 등록 userID : " + dataMap.get("userid"));

        setRedirectReponse(request, "/index.html");
    }

    private void loginUser(HttpRequest request) {
        String tmpsid = makeRandomString();
        if (isValidCredentials()) {
            User loginUser = Database.findUserById(dataMap.get("login_id"));
            session.addSession(tmpsid, loginUser);
            setRedirectReponse(request, "/main/index.html");
            httpResponse.addHeader("Set-Cookie", "sid=" + tmpsid + "; path=/");
            return ;
        }
        setRedirectReponse(request, "/login/login_failed.html");
    }

    private boolean isValidCredentials() {
        User targetUser = Database.findUserById(dataMap.get("login_id"));
        if (targetUser != null && targetUser.getPassword().equals(dataMap.get("login_password"))) {
            return true;
        }
        return false;
    }

    private String makeRandomString() {
        Random random = new Random();
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            if (random.nextBoolean()) { // true : 알파벳, false : 숫자
                randomString.append((char) (random.nextInt(26) + 65));
            }
            else {
                randomString.append(random.nextInt(10));
            }
        }

        return randomString.toString();
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

    private void makeActionMap() {
        actionMap.put("/user/create", this::addNewUser);
        actionMap.put("/user/login", this::loginUser);
    }

    private void setRedirectReponse(HttpRequest request, String path) {
        httpResponse.setResponseLine(request.getHttpVersion(), 302);
        httpResponse.addHeader("Location", path);
    }

    private void set404ErrorResponse(HttpRequest request) {
        httpResponse.setResponseLine(request.getHttpVersion(), 404);
    }
}
