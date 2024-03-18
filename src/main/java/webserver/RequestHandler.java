package webserver;

import Parser.RequestParser;
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
    private final HttpResponse httpResponse = new HttpResponse();
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

            logger.debug("Request : {}", httpRequest.getRequestLine());
            // 이 아래로 요청에 대한 처리.
            actionByMethod(dos, httpRequest);

        } catch (IOException | IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }

    private void actionByMethod(DataOutputStream dos, HttpRequest request) throws IOException {
        if (request.getMethod().equals("GET")) {
            GetMethodHandler getHandler = new GetMethodHandler();
            getHandler.sendFileResponse(dos, request, httpResponse);
            return;
        }
        else if (request.getMethod().equals("POST")) {
            PostMethodHandler postHandler = new PostMethodHandler(httpResponse);
            postHandler.actionByPath(dos, request);
            return ;
        }
        logger.error("정의되지 않은 HTTP메소드 : {}", request.getMethod());
    }
}
