package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class HttpSender {
    private static final Logger logger = LoggerFactory.getLogger(HttpSender.class);
    public void sendResponse(DataOutputStream dos, HttpResponse response) {
        try {
            dos.writeBytes(response.getResponseLine().toString());
            writeHeaders(dos, response);
            Body body = response.getBody();
            if (body != null)
                dos.write(body.getBodyContent(), 0, body.getBodyContent().length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void writeHeaders(DataOutputStream dos, HttpResponse response) {
        try {
            for (Map.Entry<String, String> entry : response.getHeaderMap().entrySet()) {
                dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
