package webserver;

import sessions.Session;

public interface HttpRequestHandler {
    public HttpResponse getResponse(HttpRequest request);
}
