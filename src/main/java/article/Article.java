package article;

public class Article {
    private final String authorName;
    private final String content;

    public Article(String authorName, String content) {
        this.authorName = authorName;
        this.content = content;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }
}
