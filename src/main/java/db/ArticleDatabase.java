package db;

import article.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleDatabase {
    private static final List<Article> articles = new ArrayList<>();

    public static void addArticle(Article article) {
        articles.add(article);
    }

    public static List<Article> getArticles() {
        return articles;
    }

    public static int getArticleCount() {
        return articles.size();
    }
}