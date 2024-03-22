package parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import http.HttpRequest;
import http.RequestLine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLineParserTest {

    private String basicRequestLine;
    private String basicRequestLineWithData;
    private RequestLineParser requestLineParser;
    @BeforeEach
    void beforeEach() {
        requestLineParser = new RequestLineParser();

        // RequestHandle.readRequestLine() 를 통해 요청 HTTP의 첫 줄만 들어온다고 가정한다.
        basicRequestLine = "GET /main/index.html HTTP/1.1";
        basicRequestLineWithData = "GET /main/index.html?name=test HTTP/1.1";
    }

    @Test
    @DisplayName("HTTP 요청에서 경로를 확인할 수 있다.")
    void parsePathTest() {
        RequestLine requestLine = requestLineParser.parse(basicRequestLine);
        assertThat(requestLine.getPath()).isEqualTo("/main/index.html");
    }

    @Test
    @DisplayName("HTTP 요청 path가 폴더 형태인 경우에도, 그대로 path에 저장된다.")
    void parsePathWithIndex_NoExtension() {
        String request = "GET /main HTTP/1.1";
        RequestLine requestLine = requestLineParser.parse(request);
        assertThat(requestLine.getPath()).isEqualTo("/main");
    }

    @Test
    @DisplayName("HTTP Path에 Data가 없는 경우, dataString이 비어있다.")
    void parseTest_NoData() {
        RequestLine result = requestLineParser.parse(basicRequestLine);
        assertThat(result.getDataString()).isEqualTo("");
    }
    @Test
    @DisplayName("HTTP Path에 Data가 존재하는 경우, dataString에 입력된다.")
    void parseTest_Data() {
        RequestLine result = requestLineParser.parse(basicRequestLineWithData);
        assertThat(result.getDataString()).isEqualTo("name=test");
    }

    @Test
    @DisplayName("HTTP의 Body로 문자열이 전달되었을 때, byte[] 타입인 body 필드에 저장할 수 있어야 한다.")
    void parseTest_Body() throws IOException {
        String request = "POST /index HTTP/1.1\r\n" +
                "Content-Length: 10\r\n" +
                "\r\n" +
                "StringBody";
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        RequestParser requestParser = new RequestParser();
        HttpRequest httpRequest = requestParser.parse(inputStream);

        assertThat(new String(httpRequest.getBody(), "UTF-8")).isEqualTo("StringBody");
    }
}