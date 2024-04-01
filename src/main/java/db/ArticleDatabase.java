package db;

import article.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleDatabase {
    public static final List<Article> articles = new ArrayList<>();

    public static void addArticle(Article article) {
        articles.add(article);
    }

}
