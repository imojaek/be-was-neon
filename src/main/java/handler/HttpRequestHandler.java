package handler;

import http.HttpRequest;
import http.HttpResponse;

public interface HttpRequestHandler {
    public HttpResponse getResponse(HttpRequest request);
}
