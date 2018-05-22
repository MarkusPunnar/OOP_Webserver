package webserver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ToDoApp implements RequestHandler {

    private final Map<String, Map<Integer, String>> taskMap = new HashMap<>();
    private int taskCounter = 0;

    @Mapping(URI = "/todoapp/form", method = "POST")
    synchronized public Response handle(Request request) throws UnsupportedEncodingException {
        String user = request.getRequestAttributes().get("authorized-user");
        Map<String, String> responseHeaders = new HashMap<>();
        String task = request.bodyToForm().get("user_message");
        if (task != null) {
            if(taskMap.containsKey(user)) {
                Map<Integer, String> oldMap = taskMap.get(user);
                oldMap.put(taskCounter, task);
            }
            else{
                Map<Integer, String> newMap = new HashMap<>();
                newMap.put(taskCounter, task);
                taskMap.put(user, newMap);
            }
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
        String user = request.getRequestAttributes().get("authorized-user");
        Map<Integer, String> map;

        if(taskMap.containsKey(user))
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
    synchronized public Response deleteTask(Request request) {
        String uri = request.getRequestURI();
        int id = Integer.parseInt(uri.substring(uri.lastIndexOf("/") + 1));
        String user = request.getRequestAttributes().get("authorized-user");
        taskMap.get(user).remove(id);
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Location", "/todoapp/form");
        return new Response(StatusCode.FOUND, responseHeaders, null);
    }

    @Override
    public void initialize(ServerConfig sc) {
    }
}
