package webserver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class ToDoApp implements RequestHandler {

    private final Map<String, Map<Integer, String>> taskMap = new HashMap<>();
    private final List<Integer> usedIDList = new ArrayList<>();

    @Mapping(URI = "/todoapp/form", method = "POST")
    synchronized public Response handle(Request request) throws IOException {
        String user = request.getAttributes().get("authorized-user");
        Map<String, String> responseHeaders = new HashMap<>();
        String task = request.bodyToForm().get("user_message");

        int taskCounter = 0;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!usedIDList.contains(i)) {
                taskCounter = i;
                usedIDList.add(i);
                break;
            }
        }
        if (task != null) {
            addToMap(task, user, taskCounter);
        }
            int taskLength = task.split(" ").length;
            addToFile(taskCounter + " " + user + " " + task);
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

    synchronized private void addToMap(String task, String user, int taskCounter) {
            if (taskMap.containsKey(user)) {
                Map<Integer, String> oldMap = taskMap.get(user);
                oldMap.put(taskCounter, task);
            } else {
                Map<Integer, String> newMap = new HashMap<>();
                newMap.put(taskCounter, task);
                taskMap.put(user, newMap);
            }
        }

    synchronized private void addToFile(String task) throws IOException {
        int counter = 0;
        List<String> newFile = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream("private/tasks.dat"))){
            counter = dis.readInt();
            for (int i = 0; i < counter; i++) {
                newFile.add(dis.readUTF());
            }
        }

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("private/tasks.dat"))) {
            dos.writeInt(counter+1);
            for (String line:newFile) {
                dos.writeUTF(line);
            }
            dos.writeUTF(task);
        }
    }

    synchronized private void removeFromFile(String id, String user) throws IOException {

        List<String> newFile = new ArrayList<>();
        int counter = 0;

        try (DataInputStream dis = new DataInputStream(new FileInputStream("private/tasks.dat"))) {
            counter = dis.readInt();
            for (int i = 0; i < counter; i++) {
                String line = dis.readUTF();
                String[] parts = line.split(" ");
                if (!parts[0].equals(id) || !parts[1].equals(user)) {
                    newFile.add(line);
                }
            }
        }

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("private/tasks.dat"))) {
            for (String line : newFile) {
                dos.writeInt(counter-1);
                dos.writeUTF(line);
            }
        }
    }

    @Override
    public void initialize(ServerConfig sc) throws IOException {

        /*FOR NULLING THE TASKS FILE
        try(DataOutputStream dos = new DataOutputStream(new FileOutputStream("private/tasks.dat"))){
            dos.writeInt(0);
        }*/

        try (DataInputStream dis = new DataInputStream(new FileInputStream("private/tasks.dat"))) {

            int count = dis.readInt();
            for (int i = 0; i < count; i++) {
                String line = dis.readUTF();
                String[] parts = line.split(" ");
                int id = Integer.parseInt(parts[0]);
                usedIDList.add(id);
                String user = parts[1];
                String task = "";
                for (int j = 2; j < parts.length; j++) {
                    if(j!=parts.length-1)
                        task+=parts[j] + " ";
                    else
                        task+=parts[j];
                }
                addToMap(task, user, id);
            }
        }
    }
}