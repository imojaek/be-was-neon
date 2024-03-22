package webserver;

import http.HttpResponse;
import http.StatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    HttpResponse response;
    @BeforeEach
    void beforeEach() {
        response = new HttpResponse();
    }

    @Test
    @DisplayName("200이라는 숫자로 응답코드를 설정하려는 경우, StatusCode.OK 가 부여되어야 한다.")
    void setStatusCode_Integer() {
        response.setResponseLine("HTTP/1.1", 200);

        try {
            Field statusCode = response.getClass().getDeclaredField("statusCode");
            statusCode.setAccessible(true);
            assertThat(statusCode.get(response)).isEqualTo(StatusCode.OK);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("StatusCode를 이용해서 응답코드를 설정하려는 경우, 매개와 같은 StatusCode가 부여되어야 한다.")
    void setStatusCode_StatusCodeEnum() {
        response.setResponseLine("HTTP/1.1", StatusCode.OK);

        try {
            Field statusCode = response.getClass().getDeclaredField("statusCode");
            statusCode.setAccessible(true);
            assertThat(statusCode.get(response)).isEqualTo(StatusCode.OK);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("25 바이트길이의 Body를 설정할 때, 헤더에 Content-Length에 대한 정보가 25로 주어져야한다.")
    void setBody_makeContentLengthHeader() {
        byte[] testBody = "testContentLength_setBody".getBytes(); // 25 Bytes

        response.setBody(testBody);
//        try {
//            Field headersField = response.getClass().getDeclaredField("headers");
//            headersField.setAccessible(true);
//            Map<String, String> headers = (Map<String, String>) headersField.get(response);
//            assertThat(headers.get("Content-Length")).isEqualTo("25");
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
        assertThat(response.getHeader("Content-Length")).isEqualTo("25");
    }
}