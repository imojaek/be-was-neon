package handler;

import handler.post.CreateUserHandler;
import handler.post.LoginHandler;
import handler.post.LogoutHandler;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PostMethodHandler implements HttpRequestHandler {
    private final Map<String, Action> actionMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(PostMethodHandler.class);

    public PostMethodHandler() {
        makeActionMap();
    }

    @Override
    public HttpResponse getResponse(HttpRequest request) {
        return actionByPath(request);
    }

    public HttpResponse actionByPath(HttpRequest request){
        for (String definedPath : actionMap.keySet()) {
            if (definedPath.equals(request.getPath())) {
                return actionMap.get(definedPath).action(request);
            }
        }
        logger.error("POST 메시지에 해당하는 메소드가 없습니다. : " + request.getRequestLine().toString());
        HttpResponseManager httpResponseManager = new HttpResponseManager();
        httpResponseManager.set404ErrorResponse(request);
        return httpResponseManager.getHttpResponse();
    }

    private void makeActionMap() {
        actionMap.put("/user/create", new CreateUserHandler());
        actionMap.put("/user/login", new LoginHandler());
        actionMap.put("/user/logout", new LogoutHandler());
    }
}
