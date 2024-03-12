package Parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLineParserTest {

    public static String basicRequestLine;
    @BeforeAll
    static void beforeAll() {
        // RequestHandle.readRequestLine() 를 통해 요청 HTTP의 첫 줄만 들어온다고 가정한다.
        basicRequestLine = "GET /main/index.html HTTP/1.1";
    }

    @Test
    @DisplayName("HTTP 요청에서 메소드를 확인할 수 있다.")
    void parseMethodTest() {
        String result = RequestLineParser.parseMethod(basicRequestLine);
        assertThat(result).isEqualTo("GET");
    }

    @Test
    @DisplayName("HTTP 요청에서 경로를 확인할 수 있다.")
    void parsePathTest() {
        String result = RequestLineParser.parsePath(basicRequestLine);
        assertThat(result).isEqualTo("/main/index.html");
    }

    @Test
    @DisplayName("HTTP 요청에서 HTTP 프로토콜 버전을 확인할 수 있다.")
    void parseVersionTest() {
        String result = RequestLineParser.parseVersion(basicRequestLine);
        assertThat(result).isEqualTo("HTTP/1.1");
    }

    @Test
    @DisplayName("HTTP 요청 path의 확장자가 없는 경우 path/index.html을 반환한다.")
    void parsePathWithIndex_NoExtension() {
        String returnPath = RequestLineParser.parsePathWithIndex("/registration");
        assertThat(returnPath).isEqualTo("/registration/index.html");
    }

    @Test
    @DisplayName("HTTP 요청 path가 폴더인 경우 path/index.html을 반환한다.")
    void parsePathWithIndex_EndWithSlash() {
        String returnPath = RequestLineParser.parsePathWithIndex("/registration/");
        assertThat(returnPath).isEqualTo("/registration/index.html");
    }
}