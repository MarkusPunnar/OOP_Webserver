package webserver;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AuthenticationUtil {

    public static Map<String,String> readHashedPasswordsToMap(Path filePath) throws IOException {
        Map<String, String> currentUsers = new HashMap<>();
        try (Scanner sc = new Scanner(filePath, "UTF-8")) {
            while (sc.hasNextLine()) {
                String user = sc.nextLine();
                String[] userInfoParts = user.split(": ");
                if (userInfoParts.length != 2) {
                    throw new RuntimeException("Invalid password format.");
                }
                currentUsers.put(userInfoParts[0], userInfoParts[1]);
            }
        }
        return currentUsers;
    }
}
