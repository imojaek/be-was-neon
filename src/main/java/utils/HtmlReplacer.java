package utils;

public class HtmlReplacer {
    private static final String TABLE_TAG = "{{{TABLE}}}";
    private static final String LOGIN_TXT = "로그인";
    private static final String LOGIN_BUTTON = "href=\"/login\">";

    public static String replaceTable(String origin, String replacement) {
        return replaceTable(origin, TABLE_TAG, replacement);
    }

    public static String replaceLoginText(String origin, String replacement) {
        return replaceTable(origin, LOGIN_TXT, replacement);
    }

    public static String replaceLoginButtonHref(String origin, String href) {
        String replacement = "href=\"" + href + "\">";
        return replaceTable(origin, LOGIN_BUTTON, replacement);
    }

    private static String replaceTable(String origin, String target, String replacement) {
        return origin.replace(target, replacement);
    }
}
