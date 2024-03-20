package webserver;

import Parser.RequestParser;
import Sessions.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final Socket connection;
    private final RequestParser requestParser = new RequestParser();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
//        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
//                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            HttpRequest httpRequest = requestParser.parse(in);
           //  logRequest(httpRequest);

            logger.debug("Request : {}", httpRequest.getRequestLine());
            // 요청에 대한 처리.
            HttpResponse response = actionByMethod(httpRequest);

            // 서버의 처리로 나온 HTTP 응답을 발송한다.
            response.sendResponse(dos);

        } catch (IOException | IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }

    private void logRequest(HttpRequest request) {
        logger.debug("Request : {}", request.toString());
    }

    private HttpResponse actionByMethod(HttpRequest request) throws IOException {
        if (request.getMethod().equals("GET")) {
            GetMethodHandler getHandler = new GetMethodHandler();
            return getHandler.sendFileResponse(request);
        }
        else if (request.getMethod().equals("POST")) {
            PostMethodHandler postHandler = new PostMethodHandler();
            return postHandler.actionByPath(request);
        }
        logger.error("정의되지 않은 HTTP메소드 : {}", request.getMethod());
        return set404ErrorResponse(request);
    }

    // 404error
    private HttpResponse set404ErrorResponse(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        response.setResponseLine(request.getHttpVersion(), 404);
        return response;
    }
}
