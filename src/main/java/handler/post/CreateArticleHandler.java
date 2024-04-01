package handler.post;

import article.ArticleManager;
import handler.UrlRequestHandler;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponseManager;
import model.User;
import sessions.Session;

import java.util.Map;

public class CreateArticleHandler implements UrlRequestHandler {
    @Override
    public HttpResponse handle(HttpRequest request) {
        return createArticle(request);
    }

    private HttpResponse createArticle(HttpRequest request) {
        HttpResponseManager httpResponseManager = new HttpResponseManager();
        ArticleManager articleManager = new ArticleManager();
        Map<String, String> dataMap = request.getBodyDataMap();
        User author = Session.getUserBySid(request.getSessionId());
        String content = dataMap.get("content");

        articleManager.addArticle(author.getName(), content);
        httpResponseManager.setRedirectReponse(request, "/");
        return httpResponseManager.getHttpResponse();
    }
}
