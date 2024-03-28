package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class HttpResponse {
    ResponseLine responseLine;
    private final Header headers = new Header();
    private Body body = null;
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    public void sendResponse(DataOutputStream dos) {
        try {
            dos.writeBytes(responseLine.toString());
            writeHeaders(dos);
            if (body != null)
                dos.write(body.getBodyContent(), 0, body.getBodyContent().length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void writeHeaders(DataOutputStream dos) {
        try {
            for (Map.Entry<String, String> entry : headers.getHeaders().entrySet()) {
                dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void setResponseLine(String version, int statusCodeNum) {
        this.responseLine = new ResponseLine(version, statusCodeNum);
    }

    public void setResponseLine(String version, StatusCode statusCode) {
        this.responseLine = new ResponseLine(version, statusCode);
    }
    public void addHeader(String name, String value) {
        headers.addHeader(name, value);
    }

    public void addCookie(String name, String value) {
        headers.addCookie(name, value);
    }

    public void makeCookieExpired() {
        headers.addCookie("Max-Age",  "0");
    }

    public void setBody(byte[] bodyContent) {
        addHeader("Content-Length", String.valueOf(bodyContent.length));
        this.body = new Body(bodyContent);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }
}
