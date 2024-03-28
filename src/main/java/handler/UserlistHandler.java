package handler;

import db.Database;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponseManager;
import http.StatusCode;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sessions.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

public class UserlistHandler implements Action{
    HttpResponseManager httpResponseManager = new HttpResponseManager();
    private static final String BASE_PATH = "./src/main/resources/static";
    private static final Logger logger = LoggerFactory.getLogger(UserlistHandler.class);

    @Override
    public HttpResponse action(HttpRequest request) {
        if (Session.isValidSession(request.getCookie("sid")))
            return sendUserList(request);
        httpResponseManager.setRedirectReponse(request, "/");
        return httpResponseManager.getHttpResponse();
    }

    private HttpResponse sendUserList(HttpRequest request) {
        try {
            byte[] html = readFileByte(BASE_PATH + "/user/list.html");
            String htmlString = new String(html, StandardCharsets.UTF_8);
            String table = makeTable();
            htmlString = htmlString.replace("{{{TABLE}}}", table);
            byte[] body = htmlString.getBytes();
            httpResponseManager.setResponseLine(request.getHttpVersion(), 200);
            httpResponseManager.addHeader("Content-Type", "text/html");
            httpResponseManager.setBody(body);
            return httpResponseManager.getHttpResponse();
        } catch (IOException e) {
            logger.error("파일을 읽던 중 오류가 발생했습니다. : " + e.getMessage());
            httpResponseManager.set404ErrorResponse(request);

            return httpResponseManager.getHttpResponse();
        }
    }

    private byte[] readFileByte(String path) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(path))) {
            byte[] bytes = fis.readAllBytes();
            return bytes;
        }
    }

    private String makeTable () {
        Collection<User> userList = Database.findAll();
        StringJoiner tableStringJoiner = new StringJoiner("\n</tr>\n<tr>\n", "<table border=\"1\">\n" +
                "<tr>\n" +
                "<th>ID</th><th>Name</th><th>Email</th>\n" +
                "</tr>\n", "\n</tr>\n</table>");

        for (User user : userList) {
            StringJoiner columnStringJoiner = new StringJoiner("</td><td>", "<td>", "</td>");
            columnStringJoiner.add(user.getUserId());
            columnStringJoiner.add(user.getName());
            columnStringJoiner.add(user.getEmail());
            tableStringJoiner.add(columnStringJoiner.toString());
        }

        return tableStringJoiner.toString();
    }
}
