package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HttpResponse {
    ResponseLine responseLine;
    private final Header header = new Header();
    private Body body = null;
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    public void setResponseLine(String version, int statusCodeNum) {
        this.responseLine = new ResponseLine(version, statusCodeNum);
    }

    public void setResponseLine(String version, StatusCode statusCode) {
        this.responseLine = new ResponseLine(version, statusCode);
    }
    public void addHeader(String name, String value) {
        header.addHeader(name, value);
    }

    public void addCookie(String name, String value) {
        header.addCookie(name, value);
    }

    public void makeCookieExpired() {
        header.addCookie("Max-Age",  "0");
    }

    public void setBody(byte[] bodyContent) {
        addHeader("Content-Length", String.valueOf(bodyContent.length));
        this.body = new Body(bodyContent);
    }

    public ResponseLine getResponseLine() {
        return responseLine;
    }

    public Map<String, String>  getHeaderMap() {
        return header.getHeaderMap();
    }

    public String getHeader(String name) {
        return header.get(name);
    }

    public Body getBody() {
        return body;
    }
}
