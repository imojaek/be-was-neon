package handler.get;

import handler.UrlRequestHandler;
import http.ContentType;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sessions.Session;
import utils.HtmlReplacer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SendFileHandler implements UrlRequestHandler {
    private static final String BASE_PATH = "./src/main/resources/static";
    HttpResponseManager httpResponseManager = new HttpResponseManager();
    private static final Logger logger = LoggerFactory.getLogger(SendFileHandler.class);

    @Override
    public HttpResponse handle(HttpRequest request) {
        return sendFileResponse(request);
    }

    // sendFile이 실행되는 경우는 Method가 GET이고, Target이 특정한 동작을 요구하지 않는 상황인 경우입니다.
    public HttpResponse sendFileResponse(HttpRequest request){
        try {
            httpResponseManager.setResponseLine(request.getHttpVersion(), 200);
            ContentType contentType = getContentTypeByPath(request.getPath()); // 확장자가 없는 폴더의 경우, index.html을 호출할 것이므로 HTML을 반환할 것입니다.
            httpResponseManager.addHeader("Content-Type", contentType.getContentTypeMsg() + ";charset=utf-8");
            byte[] body = readFileByte(modifyRequestPath(request));
            if (modifyRequestPath(request).equals(BASE_PATH + "/index.html")) {
                body = refreshMainPage(request, body);
            }
            httpResponseManager.setBody(body);

            return httpResponseManager.getHttpResponse();
        } catch (IOException e) {
            logger.error("파일을 읽던 중 오류가 발생했습니다. : " + e.getMessage());
            httpResponseManager.set404ErrorResponse(request);

            return httpResponseManager.getHttpResponse();
        }
    }

    private byte[] refreshMainPage(HttpRequest request, byte[] body) {
        if (!Session.isValidSession(request.getSessionId())) {
            return body;
        }
        String bodyString = new String(body, StandardCharsets.UTF_8);
        String userId = Session.getUserBySid(request.getSessionId()).getUserId();
        bodyString = HtmlReplacer.replaceLoginButton(bodyString, "/user/list", userId + "님, 환영합니다!");
        return bodyString.getBytes();
    }

    private String modifyRequestPath(HttpRequest request) {
        String requestPath = request.getPath();
        if (requestPath.endsWith("/")) {
            return BASE_PATH + requestPath + "index.html";
        }
        if (!requestPath.contains(".")) {
            return BASE_PATH + requestPath + "/index.html";
        }
        return BASE_PATH + requestPath;
    }

    // 파일의 경로를 매개로 받아 해당 파일의 내용을 반환
    private byte[] readFileByte(String path) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(path))) {
            byte[] bytes = fis.readAllBytes();
            return bytes;
        }
    }
    private ContentType getContentTypeByPath(String path) {
        // 경로에 .이 없어 index.html 을 참조하게 되는 경우.
        if (!path.contains("."))
            return ContentType.HTML;

        String ext = path.substring(path.lastIndexOf(".") + 1).toUpperCase();
        for (ContentType type : ContentType.values()) {
            if (type.name().equals(ext)) {
                return ContentType.valueOf(ext);
            }
        }
        throw new IllegalArgumentException("Invalid file extension. : " + path); //  지원하지 않는 확장자인 경우.
    }

}
