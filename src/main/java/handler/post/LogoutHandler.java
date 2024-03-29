package handler.post;

import handler.UrlRequestHandler;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponseManager;
import sessions.Session;

import java.util.Map;

public class LogoutHandler implements UrlRequestHandler {
    HttpResponseManager httpResponseManager;

    @Override
    public HttpResponse handle(HttpRequest request) {
        httpResponseManager = new HttpResponseManager();
        logoutUser(request);
        return httpResponseManager.getHttpResponse();
    }

    // 유효한 쿠키가 있으면 쿠키 만료 및 세션 제거
    // 유효한 쿠키가 없는 경우, "/" 로 리다이렉트
    private void logoutUser(HttpRequest request) {
        if (request.getCookies().isPresent()) {
            String sid = request.getSessionId();
            Map<String, String> cookies = request.getCookies().get();
            if (Session.isValidSession(sid)) {
                Session.deleteSession(sid);
                httpResponseManager.setRedirectReponse(request, "/");
                for (String s : cookies.keySet()) {
                    httpResponseManager.addCookie(s, cookies.get(s));
                }
                httpResponseManager.addCookie("Path", "/");
                httpResponseManager.makeCookieExpired();
                return ;
            }
        }
        httpResponseManager.setRedirectReponse(request, "/");
    }
}
