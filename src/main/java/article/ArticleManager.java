package article;

import db.ArticleDatabase;

public class ArticleManager {
    public void addArticle(String author, String content) {
        ArticleDatabase.addArticle(new Article(author, content));
    }
}
