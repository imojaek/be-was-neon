package article;

import db.ArticleDatabase;

import java.util.List;

public class ArticleManager {
    public void addArticle(String author, String content) {
        ArticleDatabase.addArticle(new Article(author, content));
    }

    public boolean hasArticle() {
        if (ArticleDatabase.getArticleCount() > 0) {
            return true;
        }
        return false;
    }

    public List<Article> getArticles() {
        return ArticleDatabase.getArticles();
    }

    public Article getLatestArticle() {
        int articleCount = ArticleDatabase.getArticleCount();
        return getArticles().get(articleCount - 1);
    }
}
