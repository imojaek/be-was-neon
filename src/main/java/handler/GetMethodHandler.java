package handler;

import handler.get.SendFileHandler;
import handler.get.UserlistHandler;
import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GetMethodHandler implements HttpMethodHandler {
    private final Map<String, UrlRequestHandler> actionMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(GetMethodHandler.class);

    public GetMethodHandler() {
        makeActionMap();
    }

    @Override
    public HttpResponse getResponse(HttpRequest request) {
        return actionByPath(request);
    }

    // 요청 경로로의 지정된 행동이 있는지 확인하고, 아닌 경우 파일을 반환하도록 한다.
    private HttpResponse actionByPath(HttpRequest request) {
        for (String definedPath : actionMap.keySet()) {
            if (definedPath.equals(request.getPath())) {
                return actionMap.get(definedPath).handle(request);
            }
        }
        // 지정된 Path로의 요청이 아닌 경우 해당 Path의 정적파일을 반환
        return new SendFileHandler().handle(request);
    }

    private void makeActionMap() {
        actionMap.put("/user/list", new UserlistHandler());
    }
}
