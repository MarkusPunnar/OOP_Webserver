package webserver;

import java.util.Map;

public class AuthUserInfo {

    private Map<String, String> currentRegisteredUsers;

    public AuthUserInfo(Map<String, String> currentRegisteredUsers) {
        this.currentRegisteredUsers = currentRegisteredUsers;
    }

    public Map<String, String> getCurrentRegisteredUsers() {
        return currentRegisteredUsers;
    }
}
