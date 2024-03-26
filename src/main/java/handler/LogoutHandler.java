package handler;

import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponseManager;
import sessions.Session;

public class LogoutHandler implements Action{
    HttpResponseManager httpResponseManager;

    @Override
    public HttpResponse action(HttpRequest request) {
        httpResponseManager = new HttpResponseManager();
        logoutUser(request);
        return httpResponseManager.getHttpResponse();
    }

    private void logoutUser(HttpRequest request) {
        if (request.getCookies().isPresent()) {
            String sid = request.getCookies().get().get("sid");
            if (Session.isValidSession(sid)) {
                Session.deleteSession(sid);
                httpResponseManager.setRedirectReponse(request, "/");
                return ;
            }
        }
        httpResponseManager.setRedirectReponse(request, "/");
    }
}
