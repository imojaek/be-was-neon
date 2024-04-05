package webserver;

import handler.GetMethodHandler;
import handler.HttpMethodHandler;
import handler.PostMethodHandler;
import handler.UndefinedMethodHandler;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponseManager;
import http.HttpSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.RequestParser;

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
            HttpSender  httpSender = new HttpSender();
            httpSender.sendResponse(dos, response);

        } catch (IOException e) {
            HttpResponseManager httpResponseManager = new HttpResponseManager();
            httpResponseManager.set500ErrorResponse();
            logger.error(e.getMessage());
        }
    }

    private HttpResponse actionByMethod(HttpRequest request) {
        // 요청경로의 권한을 확인하고, 사용자가 필요한 권한을 가지고 있는지 확인합니다.
        SecurityManager securityManager = new SecurityManager();
        if (!securityManager.hasValidatePermission(request)) {
            HttpResponseManager httpResponseManager = new HttpResponseManager();
            httpResponseManager.setRedirectReponse(request, "/login");
            return httpResponseManager.getHttpResponse();
        }

        HttpMethodHandler HttpMethodHandler;

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
