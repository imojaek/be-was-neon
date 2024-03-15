package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    ResponseLine responseLine;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body = null;
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    public void sendResponse(DataOutputStream dos) {
        try {
            dos.writeBytes(responseLine.toString());
            writeHeaders(dos);
            if (body != null)
                dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void writeHeaders(DataOutputStream dos) {
        try {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
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
        headers.put(name, value);
    }

    public void setBody(byte[] body) {
        addHeader("Content-Length", String.valueOf(body.length));
        this.body = body;
    }
}
