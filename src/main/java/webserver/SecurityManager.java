package webserver;

import http.HttpRequest;
import sessions.Session;

public class SecurityManager {

    // 사용자가 요청경로에 필요한 권한을 가지고 있는지 확인한다.
    public boolean hasValidatePermission(HttpRequest request) {
        PathSecurity pathSecurity = checkPathSecurity(request.getPath());
        if (pathSecurity == PathSecurity.NEED_LOGIN && !Session.isValidSession(request.getSessionId())) {
            return false;
        }
        return true;
    }

    private PathSecurity checkPathSecurity(String path) {
        return PathSecurity.getRequiredSecurityLevel(path);
    }
}
