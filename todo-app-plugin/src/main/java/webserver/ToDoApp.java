package webserver;

import java.io.*;
import java.util.*;

public class ToDoApp implements RequestHandler {

    private final Map<Integer, String> toDoList = new HashMap<>();
    private int taskCounter = 0;

    @Mapping(URI = "/todoapp/form", method = "POST")
    synchronized public Response handle(Request request) throws UnsupportedEncodingException {
        Map<String, String> responseHeaders = new HashMap<>();
        String task = request.bodyToForm().get("user_message");
        if (task != null) {
            toDoList.put(taskCounter, task);
            taskCounter++;
        }
        responseHeaders.put("Location", "/todoapp/form");
        return new Response(StatusCode.FOUND, responseHeaders, null);
    }


    @Mapping(URI = "/todoapp/form")
    synchronized public Response getList(Request request) throws IOException {

        String template = new String(WebServerUtil.readFileFromClasspath("app.html"), "UTF-8");
        Map<String, String> responseHeaders = new HashMap<>();
        String existingItems = "";
        for (int taskID : toDoList.keySet()) {
            existingItems += "<li>" + toDoList.get(taskID) +
                    "<form action=\"/todoapp/delete/" + taskID + "\" method=\"post\">\n" +
                    "<button type=\"submit\">Done</button>\n</form></li>";
        }
        if (toDoList.size() == 0)
            template = template.replace("$$EXISTING$$", "No tasks");
        String response = template.replace("$$EXISTING$$", existingItems);
        byte[] body = response.getBytes("UTF-8");
        return new Response(StatusCode.OK, responseHeaders, body);
    }

    @Mapping(URI = "/todoapp/delete/*", method = "POST")
    synchronized public Response deleteTask(Request request) throws UnsupportedEncodingException {
        String uri = request.getRequestURI();
        int id = Integer.parseInt(uri.substring(uri.lastIndexOf("/") + 1));
        toDoList.remove(id);
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Location", "/todoapp/form");
        return new Response(StatusCode.FOUND, responseHeaders, null);
    }

    @Override
    public void initialize(ServerConfig sc) {
    }

    public void getPluginName() {
        System.out.println("ToDoApp Plugin");
    }
}
