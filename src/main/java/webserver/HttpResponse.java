package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private String version = "";
    private StatusCode statusCode;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body = null;
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    public void sendResponse(DataOutputStream dos) {
        try {
            dos.writeBytes(version + " " + statusCode.toString() + " \r\n");
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

    public void setVersion(String version) {
        this.version = version;
    }
    public void setStatusCode(int statusCodeNum) {
        for (StatusCode value : StatusCode.values()) {
            if (value.isEqualTo(statusCodeNum))
                this.statusCode = value;
        }
    }
    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setBody(byte[] body) {
        addHeader("Content-Length", String.valueOf(body.length));
        this.body = body;
    }
}
