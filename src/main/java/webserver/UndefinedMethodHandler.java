package webserver;

import Sessions.Session;

public class UndefinedMethodHandler implements HttpRequestHandler {
    @Override
    public HttpResponse getResponse(HttpRequest request, Session session) {
        HttpResponseManager httpResponseManager = new HttpResponseManager();
        httpResponseManager.set404ErrorResponse(request);
        return httpResponseManager.getHttpResponse();
    }
}
