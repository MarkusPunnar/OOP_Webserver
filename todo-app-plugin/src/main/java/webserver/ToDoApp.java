package webserver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class ToDoApp implements RequestHandler {

    private final Map<String, Map<Integer, String>> taskMap = new HashMap<>();
    private final List<String> usedIDList = new ArrayList<>();

    @Mapping(URI = "/todoapp/form", method = "POST")
    synchronized public Response handle(Request request) throws IOException {
        String user = request.getAttributes().get("authorized-user");
        Map<String, String> responseHeaders = new HashMap<>();
        String task = request.bodyToForm().get("user_message");

        int taskCounter = 0;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!usedIDList.contains(String.valueOf(i))) {
                taskCounter = i;
                usedIDList.add(String.valueOf(i));
                break;
            }
        }

        if (task != null) {
            if (taskMap.containsKey(user)) {
                Map<Integer, String> oldMap = taskMap.get(user);
                oldMap.put(taskCounter, task);
            } else {
                Map<Integer, String> newMap = new HashMap<>();
                newMap.put(taskCounter, task);
                taskMap.put(user, newMap);
            }
            addToFile(taskCounter + " " + user + " " + task);
            taskCounter++;
        }
        responseHeaders.put("Location", "/todoapp/form");
        return new Response(StatusCode.FOUND, responseHeaders, null);
    }


    @Mapping(URI = "/todoapp/form")
    synchronized public Response getList(Request request) throws IOException {

        String template = new String(WebServerUtil.readFileFromClasspath("app.html"), "UTF-8");
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "text/html");
        String existingItems = "";
        String user = request.getAttributes().get("authorized-user");
        Map<Integer, String> map;

        if (taskMap.containsKey(user))
            map = taskMap.get(user);
        else {
            map = new HashMap<>();
            taskMap.put(user, map);
        }

        for (int taskID : map.keySet()) {
            existingItems += "<li>" + map.get(taskID) +
                    "<form action=\"/todoapp/delete/" + taskID + "\" method=\"post\">\n" +
                    "<button type=\"submit\">Done</button>\n</form></li>";
        }

        if (map.size() == 0)
            template = template.replace("$$EXISTING$$", "No tasks");
        String response = template.replace("$$EXISTING$$", existingItems);
        byte[] body = response.getBytes("UTF-8");
        return new Response(StatusCode.OK, responseHeaders, body);
    }

    @Mapping(URI = "/todoapp/delete/*", method = "POST")
    synchronized public Response deleteTask(Request request) throws IOException {
        String uri = request.getRequestURI();
        int id = Integer.parseInt(uri.substring(uri.lastIndexOf("/") + 1));
        String user = request.getAttributes().get("authorized-user");
        removeFromFile(String.valueOf(id), user);
        taskMap.get(user).remove(id);
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Location", "/todoapp/form");
        return new Response(StatusCode.FOUND, responseHeaders, null);
    }

    synchronized private void addToFile(String task) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("private/tasks.txt"))) {
            dos.writeUTF(task);
        }
    }

    synchronized private void removeFromFile(String id, String user) throws IOException {

        List<String> newFile = new ArrayList<>();

        try (DataInputStream dis = new DataInputStream(new FileInputStream("private/tasks.txt"))) {
            String line = dis.readUTF();
            while (line != null) {
                String[] parts = line.split(" ");
                if (!parts[0].equals(id) || !parts[1].equals(user)) {
                    newFile.add(line);
                }
            }
        }

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("private/tasks.txt"))) {
            String usedIDString = "";
            for (String usedID : usedIDList) {
                usedIDString += usedID + " ";
            }
            dos.writeUTF(usedIDString);
            for (String line : newFile) {
                dos.writeUTF(line);
            }
        }
    }

    @Override
    public void initialize(ServerConfig sc) throws IOException {
        /*try (DataInputStream dis = new DataInputStream(new FileInputStream("private/tasks.txt"))) {

            String line = dis.readUTF();
            if (line != null) {
                String[] IDListAsString = line.split(" ");
                for (String id : IDListAsString) {
                    if (!id.equals(""))
                        this.usedIDList.add(id);
                }
            }

            while (line != null) {
                String[] parts = line.split(" ");
                String user = parts[1];
                if (taskMap.containsKey(user)) {
                    Map<Integer, String> oldMap = taskMap.get(user);
                    oldMap.put(Integer.parseInt(parts[0]), parts[2]);
                } else {
                    Map<Integer, String> newMap = new HashMap<>();
                    newMap.put(Integer.parseInt(parts[0]), parts[2]);
                    taskMap.put(user, newMap);
                }
            }
        }*/
    }
}