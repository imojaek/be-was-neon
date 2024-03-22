package webserver;

import handler.GetMethodHandler;
import handler.HttpRequestHandler;
import handler.PostMethodHandler;
import handler.UndefinedMethodHandler;
import http.HttpRequest;
import http.HttpResponse;
import parser.RequestParser;
import sessions.Session;
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

            if (httpRequest.getPath().endsWith(".html") && hasCookie(httpRequest)) {
                if (httpRequest.getCookies().isPresent()) {
                    String sid = httpRequest.getCookies().get().get("sid");
                    if (Session.isValidSession(sid)) {
                        logger.debug("현재 세션의 UserId : {}, sid : {}", Session.getUserBySid(sid).getUserId(), sid);
                    }
                }
            }
            logger.debug("Request : {}", httpRequest.getRequestLine());
            // 요청에 대한 처리
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
    private boolean hasCookie(HttpRequest request) {
        return request.getCookies().isPresent();
    }

    private HttpResponse actionByMethod(HttpRequest request) throws IOException {
        HttpRequestHandler httpRequestHandler;
        if (request.getMethod().equals("GET")) {
            httpRequestHandler = new GetMethodHandler();
        }
        else if (request.getMethod().equals("POST")) {
            httpRequestHandler = new PostMethodHandler();
        }
        else {
            httpRequestHandler = new UndefinedMethodHandler();
            logger.error("정의되지 않은 HTTP메소드 : {}", request.getMethod());
        }
        return httpRequestHandler.getResponse(request);
    }
}
