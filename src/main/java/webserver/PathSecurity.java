package webserver;

import java.nio.file.Path;
import java.util.List;

public enum PathSecurity {
    NEED_LOGIN(List.of("/user/list", "/article")),
    PUBLIC(List.of());

    private final List<String> pathList;

    PathSecurity(List<String> pathList) {
        this.pathList = pathList;
    }

    public List<String> getPathList() {
        return pathList;
    }

    // HTTP 요청의 경로가 특정 상수(권한 단계)의 필드리스트에 포함되는 경우, 그 상수를 반환해서 어떤 권한이 필요한 지 알린다.
    public static PathSecurity getRequiredSecurityLevel(String path) {
        for (PathSecurity security : PathSecurity.values()) {
            if (security.getPathList().contains(path)) {
                return security;
            }
        }
        return PathSecurity.PUBLIC;
    }
}
