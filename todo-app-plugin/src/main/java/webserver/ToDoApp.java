package webserver;

import java.io.*;
import java.util.*;

public class ToDoApp implements RequestHandler{

    Map<Integer, String> toDoList = new HashMap<>();
    int taskCounter = 0;
    @Mapping(URI = "/todoapp/form", method = "POST")
    public Response handle(Request request) throws UnsupportedEncodingException {
        Map<String, String> responseHeaders = new HashMap<>();
        String task = request.bodyToForm().get("user_message");
        if(task!=null) {
            toDoList.put(taskCounter, task);
            taskCounter++;
        }
        responseHeaders.put("Location", "/todoapp/form");
        return new Response(StatusCode.FOUND, responseHeaders, null);
    }

    @Mapping(URI = "/todoapp/form")
    public Response getList(Request request) throws IOException {
        String template = "";
        template = new String(WebServerUtil.readFileFromClasspath("app.html"), "UTF-8");
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body;
        String existingItems = "";
        for (int taskID:toDoList.keySet()) {
            existingItems+="<li>" + toDoList.get(taskID) +
                    "<form enctype=\"text/plain\" action=\"/todoapp/delete/" + taskID + "\" method=\"post\">\n" +
                    "<button type=\"submit\">Done</button>\n</form></li><br>";
        }
        if(toDoList.size()==0)
            template = template.replace("$$EXISTING$$", "No tasks");
        String response = template.replace("$$EXISTING$$", existingItems);
        body = response.getBytes();
        return new Response(StatusCode.OK, responseHeaders, body);
    }

    @Mapping(URI="/todoapp/delete/*", method="POST")
    public Response deleteTask(Request request) throws UnsupportedEncodingException {
        String uri = request.getRequestURI();
        toDoList.remove(Integer.parseInt(uri.substring(uri.length()-1,uri.length())));
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Location", "/todoapp/form");
        return new Response(StatusCode.FOUND, responseHeaders, null);
    }

    @Override
    public void initialize(ServerConfig sc) {    }
}
