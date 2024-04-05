package handler.post;

import db.Database;
import handler.UrlRequestHandler;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponseManager;
import model.User;
import sessions.Session;

import java.util.Map;
import java.util.Random;

public class LoginHandler implements UrlRequestHandler {
    private final HttpResponseManager httpResponseManager = new HttpResponseManager();
    private Map<String, String> dataMap;
    @Override
    public HttpResponse handle(HttpRequest request) {
        dataMap = request.getBodyDataMap();
        loginUser(request);
        return httpResponseManager.getHttpResponse();
    }

    private void loginUser(HttpRequest request) {
        if (isValidCredentials(dataMap)) {
            String sid = makesid();
            User loginUser = Database.findUserById(dataMap.get("login_id"));
            Session.addSession(sid, loginUser);
            httpResponseManager.setRedirectReponse(request, "/main/index.html");
            httpResponseManager.addCookie("sid", sid);
            httpResponseManager.addCookie("path", "/");
            return ;
        }
        httpResponseManager.setRedirectReponse(request, "/login/login_failed.html");
    }

    private String makesid() {
        String sid = makeRandomString();
        while (Session.isExistSession(sid)) {
            sid = makeRandomString();
        }
        return sid;
    }

    private boolean isValidCredentials(Map<String, String> dataMap) {
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
}
