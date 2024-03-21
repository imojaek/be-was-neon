package webserver;

import Sessions.Session;

public interface HttpRequestHandler {
    public HttpResponse getResponse(HttpRequest request, Session session);
}
