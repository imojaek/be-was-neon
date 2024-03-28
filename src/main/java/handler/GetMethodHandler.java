package handler;

import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import http.ContentType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GetMethodHandler implements HttpRequestHandler{
    private final Map<String, Action> actionMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(GetMethodHandler.class);

    public GetMethodHandler() {
        makeActionMap();
    }

    @Override
    public HttpResponse getResponse(HttpRequest request) {
        return actionByPath(request);
    }

    private HttpResponse actionByPath(HttpRequest request) {
        for (String definedPath : actionMap.keySet()) {
            if (definedPath.equals(request.getPath())) {
                return actionMap.get(definedPath).action(request);
            }
        }
        // 지정된 Path로의 요청이 아닌 경우 해당 Path의 정적파일을 반환
        return new SendFileHandler().action(request);
    }

    private void makeActionMap() {
        actionMap.put("/user/list", new UserlistHandler());
    }
}
