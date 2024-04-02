package utils;

public class HtmlReplacer {
    private static final String TABLE_TAG = "{{{TABLE}}}";
    private static final String AUTHOR_NAME_REG = "<!--AUTHOR_NAME_START-->((.|\\r|\\n)*?)<!--AUTHOR_NAME_END-->";
    private static final String CONTENT_REG = "<!--CONTENT_START-->((.|\\r|\\n)*?)<!--CONTENT_END-->";
    private static final String SESSION_REG = "<!--SESSION_START-->((.|\\r|\\n)*?)<!--SESSION_END-->";

    public static String replaceTable(String origin, String replacement) {
        return replaceString(origin, TABLE_TAG, replacement);
    }

    public static String makeLoginSession(String origin, String userName) {
        StringBuilder sb = new StringBuilder();
        sb.append(userButton(userName));
        sb.append(logoutButton());
        return origin.replaceAll(SESSION_REG, sb.toString());
    }

    private static String userButton(String userName) {
        return "<li class=\"header__menu__item\">\r\n" +
                "            <a class=\"btn btn_contained btn_size_s\" href=\"/user/list\">\r\n" +
                "            " + userName + "님, 환영합니다!\r\n" +
                "            </a>\r\n" +
                "          </li>\r\n";
    }

    private static String logoutButton() {
        return "<li class=\"header__menu__item\">\r\n" +
                "            <form action=\"/user/logout\" method=\"post\">\r\n" +
                "              <button id=\"logout-btn\" class=\"btn btn_contained btn_size_s\">\r\n" +
                "                로그아웃\r\n" +
                "              </button>\r\n" +
                "            </form>\r\n" +
                "          </li>\r\n";
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
