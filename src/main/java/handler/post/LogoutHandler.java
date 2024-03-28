package handler.post;

import handler.UrlRequestHandler;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponseManager;
import sessions.Session;

public class LogoutHandler implements UrlRequestHandler {
    HttpResponseManager httpResponseManager;

    @Override
    public HttpResponse handle(HttpRequest request) {
        httpResponseManager = new HttpResponseManager();
        logoutUser(request);
        return httpResponseManager.getHttpResponse();
    }

    private void logoutUser(HttpRequest request) {
        if (request.getCookies().isPresent()) {
            String sid = request.getSessionId();
            if (Session.isValidSession(sid)) {
                Session.deleteSession(sid);
                httpResponseManager.setRedirectReponse(request, "/");
                return ;
            }
        }
        httpResponseManager.setRedirectReponse(request, "/");
    }
}
