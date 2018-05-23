package webserver;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;

public class AuthUsersRegistry {

    private final Map<String, String> currentRegisteredUsers;

    public AuthUsersRegistry(Map<String, String> currentRegisteredUsers) {
        this.currentRegisteredUsers = currentRegisteredUsers;
    }

    public synchronized void add(String username, String hashedPw) {
        currentRegisteredUsers.put(username, hashedPw);
    }

    public boolean checkUser(String username, String password) {
        boolean passwordCheck = BCrypt.checkpw(password, currentRegisteredUsers.get(username));
        synchronized (currentRegisteredUsers) {
            return currentRegisteredUsers.containsKey(username) && passwordCheck;
        }
    }
}
