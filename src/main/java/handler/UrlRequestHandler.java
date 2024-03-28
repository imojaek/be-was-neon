package handler;

import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface UrlRequestHandler {
    default HttpResponse handle(HttpRequest request) {
        Class<? extends UrlRequestHandler> clazz = this.getClass();
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.error("아직 지정되지 않은 동작입니다. : " + request.getRequestLine().toString());
        return null;
    }
}
