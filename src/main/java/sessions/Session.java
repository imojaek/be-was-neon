package sessions;

import model.User;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private static Map<String, User> sessionMap = new HashMap<>();

    public void addSession(String sid, User user) {
        sessionMap.put(sid, user);
    }
    public boolean isValidSession(String sid) {
        return sessionMap.containsKey(sid);
    }
    public User getUserBySid(String sid) {
        return sessionMap.get(sid);
    }
}
