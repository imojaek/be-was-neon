package handler;

import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Action {
    default HttpResponse action(HttpRequest request) {
        Class<? extends Action> clazz = this.getClass();
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.debug("비상!!!!!!!!!!!!!!!!!");
        return null;
    }
}
