package webserver;

import handler.GetMethodHandler;
import handler.HttpMethodHandler;
import handler.PostMethodHandler;
import handler.UndefinedMethodHandler;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponseManager;
import parser.RequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sessions.Session;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MainHandler.class);
    private final Socket connection;
    private final RequestParser requestParser = new RequestParser();

    public MainHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
//        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
//                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            HttpRequest httpRequest = requestParser.parse(in);
            logger.debug("Request : {}", httpRequest.getRequestLine());

            // 요청에 대한 처리
            HttpResponse response = actionByMethod(httpRequest);

            // 서버의 처리로 나온 HTTP 응답을 발송한다.
            response.sendResponse(dos);

        } catch (IOException e) {
            HttpResponseManager httpResponseManager = new HttpResponseManager();
            httpResponseManager.set500ErrorResponse();
            logger.error(e.getMessage());
        }
    }

    private boolean isProtectedPath(HttpRequest httpRequest) {
        for (ProtectedPath value : ProtectedPath.values()) {
            if (httpRequest.getPath().equals(value.getPath()))
                return true;
        }
        return false;
    }

    private HttpResponse actionByMethod(HttpRequest request) {
        HttpMethodHandler HttpMethodHandler;
        // 권한이 있는 페이지를 요청하고 있는지, 그리고 그러한 페이지를 요청하고 있다면 로그인된 쿠키를 가지고 있는지 확인한다.
        if (isProtectedPath(request)) {
            if (!Session.isValidSession(request.getSessionId())) {
                HttpResponseManager httpResponseManager = new HttpResponseManager();
                httpResponseManager.setRedirectReponse(request, "/login");
                return httpResponseManager.getHttpResponse();
            }
        }

        if (request.getMethod().equals("GET")) {
            HttpMethodHandler = new GetMethodHandler();
        }
        else if (request.getMethod().equals("POST")) {
            HttpMethodHandler = new PostMethodHandler();
        }
        else {
            HttpMethodHandler = new UndefinedMethodHandler();
            logger.error("정의되지 않은 HTTP메소드 : {}", request.getMethod());
        }
        return HttpMethodHandler.getResponse(request);
    }
}
