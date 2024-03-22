package sessions;

import model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {
    private static final Map<String, User> sessionMap = new ConcurrentHashMap<>();

    public static void addSession(String sid, User user) {
        sessionMap.put(sid, user);
    }
    public static boolean isValidSession(String sid) {
        return sessionMap.containsKey(sid);
    }
    public static User getUserBySid(String sid) {
        return sessionMap.get(sid);
    }
}
