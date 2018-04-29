package webserver;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LoginFilter implements Filter {

    private Path directory;

    public Response doFilter(Request request) throws Exception {
        Map<String, String> currentUsers = new HashMap<>();
        List<String> loggedUsers = new ArrayList<>();
        int statusCode;
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body = null;
        Path usersFilePath = Paths.get(directory.toString(), "passwords.txt");
        String insertedUsername;
        String insertedPassword;
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Username: ");
            insertedUsername = sc.nextLine();
            System.out.print("Password: ");
            insertedPassword = sc.nextLine();
        }
        try (Scanner sc = new Scanner(usersFilePath.toFile(), "UTF-8")) {
            while (sc.hasNextLine()) {
                String user = sc.nextLine();
                String[] userInfo = user.split(": ");
                currentUsers.put(userInfo[0], userInfo[1]);
            }
        }
        if (currentUsers.get(insertedUsername) != null) {
            if (BCrypt.checkpw(insertedPassword, currentUsers.get(insertedUsername))) {
                String loginToken = BCrypt.gensalt(30);
                loggedUsers.add(loginToken);
                responseHeaders.put("Set-Cookie", "login="+loginToken);
            }
        }
        return null;
    }

    public void initialize(ServerConfig sc) {
        this.directory = sc.getDirectory();
    }
}
