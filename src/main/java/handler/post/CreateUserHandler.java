package handler.post;

import db.Database;
import handler.Action;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponseManager;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CreateUserHandler implements Action {
    private static final Logger logger = LoggerFactory.getLogger(CreateUserHandler.class);
    private final HttpResponseManager httpResponseManager = new HttpResponseManager();

    @Override
    public HttpResponse action(HttpRequest request) {
        addNewUser(request);
        return httpResponseManager.getHttpResponse();
    }

    private void addNewUser(HttpRequest request) {
        Map<String, String> dataMap = request.getBodyDataMap();
        Database.addUser(new User(dataMap.get("userid"), dataMap.get("password"), dataMap.get("name"), dataMap.get("email")));
        logger.debug("새로운 회원 등록 userID : " + dataMap.get("userid"));

        httpResponseManager.setRedirectReponse(request, "/index.html");
    }
}
