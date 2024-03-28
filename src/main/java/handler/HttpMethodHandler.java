package handler;

import http.HttpRequest;
import http.HttpResponse;

public interface HttpMethodHandler {
    public HttpResponse getResponse(HttpRequest request);
}
