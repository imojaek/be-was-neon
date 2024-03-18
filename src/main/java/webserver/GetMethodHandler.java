package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class GetMethodHandler {

    private static final String BASE_PATH = "./src/main/resources/static";
    private final HttpResponse httpResponse = new HttpResponse();


    // sendFile이 실행되는 경우는 Method가 GET이고, Target이 특정한 동작을 요구하지 않는 상황인 경우입니다.
    public HttpResponse sendFileResponse(DataOutputStream dos, HttpRequest request) throws IOException {
        httpResponse.setResponseLine(request.getHttpVersion(), 200);
        ContentType contentType = getContentTypeByPath(request.getPath()); // 확장자가 없는 폴더의 경우, index.html을 호출할 것이므로 HTML을 반환할 것입니다.
        httpResponse.addHeader("Content-Type", contentType.getContentTypeMsg() + ";charset=utf-8");
        byte[] body = readFileByte(modifyRequsetPath(request));
        httpResponse.setBody(body);

        return httpResponse;
    }

    private String modifyRequsetPath(HttpRequest request) {
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
