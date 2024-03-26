package handler;

import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Action {
    default HttpResponse action(HttpRequest request) {
        Class<? extends Action> clazz = this.getClass();
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.error("지정되지 않은 action입니다. : " + request.getRequestLine().toString());
        return null;
    }
}
