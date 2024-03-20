package webserver;

public class HttpResponseManager {
    private HttpResponse httpResponse = new HttpResponse();

    public void setResponseLine(String httpVersion, int statusCode) {
        httpResponse.setResponseLine(httpVersion, statusCode);
    }

    public void setBody(byte[] body) {
        httpResponse.setBody(body);
    }

    public void addHeader(String name, String value) {
        httpResponse.addHeader(name, value);
    }

    public void setRedirectReponse(HttpRequest request, String path) {
        httpResponse.setResponseLine(request.getHttpVersion(), 302);
        httpResponse.addHeader("Location", path);
    }

    public void set404ErrorResponse(HttpRequest request) {
        httpResponse.setResponseLine(request.getHttpVersion(), 404);
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}
