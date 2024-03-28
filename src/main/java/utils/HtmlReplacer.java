package utils;

public class HtmlReplacer {
    private static final String TABLE_TAG = "{{{TABLE}}}";
    private static final String LOGIN_TXT = "로그인";
    private static final String LOGIN_HREF = "href=\"/login\">";

    public static String replaceTable(String origin, String replacement) {
        return replaceString(origin, TABLE_TAG, replacement);
    }

    public static String replaceLoginButton(String origin, String replaceHref, String replaceTxt) {
        return replaceLoginButtonHref(replaceLoginText(origin, replaceTxt), replaceHref);
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
}
