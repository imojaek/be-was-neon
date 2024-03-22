package webserver;

import sessions.Session;

public class UndefinedMethodHandler implements HttpRequestHandler {
    @Override
    public HttpResponse getResponse(HttpRequest request) {
        HttpResponseManager httpResponseManager = new HttpResponseManager();
        httpResponseManager.set404ErrorResponse(request);
        return httpResponseManager.getHttpResponse();
    }
}
