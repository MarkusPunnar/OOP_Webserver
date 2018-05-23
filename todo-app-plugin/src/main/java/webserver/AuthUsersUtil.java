package webserver;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;

public class AuthUsersUtil {

    private Map<String, String> currentRegisteredUsers;

    public AuthUsersUtil(Map<String, String> currentRegisteredUsers) {
        this.currentRegisteredUsers = currentRegisteredUsers;
    }

    public synchronized void add(String username, String hashedPw) {
        currentRegisteredUsers.put(username, hashedPw);
    }

    public synchronized boolean checkUser(String username, String password) {
        return currentRegisteredUsers.containsKey(username) && BCrypt.checkpw(password, currentRegisteredUsers.get(username));
    }
}
