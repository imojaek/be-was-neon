package handler;

import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponseManager;

public class UndefinedMethodHandler implements HttpMethodHandler {
    @Override
    public HttpResponse getResponse(HttpRequest request) {
        HttpResponseManager httpResponseManager = new HttpResponseManager();
        httpResponseManager.set404ErrorResponse(request);
        return httpResponseManager.getHttpResponse();
    }
}
