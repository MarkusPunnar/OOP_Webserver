package webserver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationUtil {

    public static Map<String, String> readRegisteredUsersToMap() throws IOException {
        Map<String, String> registeredUsers = new HashMap<>();
        byte[] allRegisteredUsersAsByteArray = WebServerUtil.readFileFromClasspath("passwords.txt");
        if (allRegisteredUsersAsByteArray != null) {
            String allRegisteredUsers = new String(allRegisteredUsersAsByteArray, StandardCharsets.UTF_8);
            allRegisteredUsers = allRegisteredUsers.replaceAll("\r\n", "\n");
            String[] users = allRegisteredUsers.split("\n");
            for (String user : users) {
                String[] userDataParts = user.split(": ");
                registeredUsers.put(userDataParts[0], userDataParts[1]);
            }
        }
        return registeredUsers;
    }
}
