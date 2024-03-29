package http;

public class HttpResponseManager {
    private HttpResponse httpResponse = new HttpResponse();
    private static final String BASE_HTTP_VERSION = "HTTP/1.1";

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setResponseLine(String httpVersion, int statusCode) {
        httpResponse.setResponseLine(httpVersion, statusCode);
    }

    public void setBody(byte[] body) {
        httpResponse.setBody(body);
    }

    public void addHeader(String name, String value) {
        httpResponse.addHeader(name, value);
    }

    public void addCookie(String name, String value) {
        httpResponse.addCookie(name, value);
    }

    public void makeCookieExpired() {
        httpResponse.makeCookieExpired();
    }

    public void setRedirectReponse(HttpRequest request, String path) {
        httpResponse.setResponseLine(request.getHttpVersion(), 302);
        httpResponse.addHeader("Location", path);
    }

    public void set404ErrorResponse(HttpRequest request) {
        httpResponse.setResponseLine(request.getHttpVersion(), 404);
    }

    public void set500ErrorResponse() {
        httpResponse.setResponseLine(BASE_HTTP_VERSION, 500);
    }

    public void set500ErrorResponse(HttpRequest request) {
        httpResponse.setResponseLine(request.getHttpVersion(), 500);
    }
}
