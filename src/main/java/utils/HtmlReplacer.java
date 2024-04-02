package utils;

public class HtmlReplacer {
    private static final String TABLE_TAG = "{{{TABLE}}}";
    private static final String LOGIN_BUTTON = "href=\"/login\">로그인";
    private static final String LOGIN_TXT = "로그인";
    private static final String LOGIN_HREF = "href=\"/login\">";
    private static final String AUTHOR_NAME_REG = "<!--AUTHOR_NAME_START-->(.*?)<!--AUTHOR_NAME_END-->";
    private static final String CONTENT_REG = "<!--CONTENT_START-->(.*?)<!--CONTENT_END-->";

    public static String replaceTable(String origin, String replacement) {
        return replaceString(origin, TABLE_TAG, replacement);
    }

    public static String replaceLoginButton(String origin, String replaceHref, String replaceTxt) {
        String replacement = "href=\"" + replaceHref + "\">" + replaceTxt;
        return replaceString(origin, LOGIN_BUTTON, replacement);
    }

    public static String replaceLoginText(String origin, String replacement) {
        return replaceString(origin, LOGIN_TXT, replacement);
    }

    public static String replaceLoginButtonHref(String origin, String href) {
        String replacement = "href=\"" + href + "\">";
        return replaceString(origin, LOGIN_HREF, replacement);
    }

    private static String replaceString(String origin, String target, String replacement) {
        return origin.replace(target, replacement);
    }

    public static String replaceAuthorName(String originHtml, String authorName) {
        return originHtml.replaceAll(AUTHOR_NAME_REG, authorName);
    }

    public static String replaceArticleContent(String originHtml, String articleContent) {
        return originHtml.replaceAll(CONTENT_REG, articleContent);
    }
}
