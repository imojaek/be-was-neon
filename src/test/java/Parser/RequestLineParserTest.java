package Parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.RequestLine;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLineParserTest {

    public static String basicRequestLine;
    public static String basicRequestLineWithData;
    @BeforeAll
    static void beforeAll() {
        // RequestHandle.readRequestLine() 를 통해 요청 HTTP의 첫 줄만 들어온다고 가정한다.
        basicRequestLine = "GET /main/index.html HTTP/1.1";
        basicRequestLineWithData = "GET /main/index.html?name=test HTTP/1.1";
    }

    @Test
    @DisplayName("HTTP 요청에서 경로를 확인할 수 있다.")
    void parsePathTest() {
        RequestLine requestLine = RequestLineParser.parse(basicRequestLine);
        assertThat(requestLine.getPath()).isEqualTo("/main/index.html");
    }

    @Test
    @DisplayName("HTTP 요청 path의 확장자가 없는 경우 path의 끝에 /index.html이 붙어있다.")
    void parsePathWithIndex_NoExtension() {
        String request = "GET /main HTTP/1.1";
        RequestLine requestLine = RequestLineParser.parse(request);
        assertThat(requestLine.getPath()).isEqualTo("/main/index.html");
    }

    @Test
    @DisplayName("HTTP 요청 path가 폴더인 경우 path/index.html 형태로 저장된다.")
    void parsePathWithIndex_EndWithSlash() {
        String request = "GET /main/ HTTP/1.1";
        RequestLine requestLine = RequestLineParser.parse(request);
        assertThat(requestLine.getPath()).isEqualTo("/main/index.html");
    }

    @Test
    @DisplayName("HTTP Path에 Data가 없는 경우, dataString이 비어있다.")
    void parseTest_NoData() {
        RequestLine result = RequestLineParser.parse(basicRequestLine);
        assertThat(result.getDataString()).isEqualTo("");
    }
    @Test
    @DisplayName("HTTP Path에 Data가 존재하는 경우, dataString에 입력된다.")
    void parseTest_Data() {
        RequestLine result = RequestLineParser.parse(basicRequestLineWithData);
        assertThat(result.getDataString()).isEqualTo("name=test");
    }


}