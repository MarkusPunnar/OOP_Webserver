package webserver;

import java.io.*;
import java.util.*;

public class ToDoApp implements RequestHandler{

    List<String> toDoList = new ArrayList<>();

    @Mapping(URI = "/todoapp/form", method = "POST")
    public Response handle(Request request) throws UnsupportedEncodingException {
        Map<String, String> responseHeaders = new HashMap<>();
        String task = request.bodyToForm().get("user_message");
        if(task!=null)
            toDoList.add(task);
        responseHeaders.put("Location", "/todoapp/form");
        return new Response(StatusCode.FOUND, responseHeaders, null);
    }

    @Mapping(URI = "/todoapp/form")
    public Response getList(Request request) throws IOException {
        String template = "";
        try(FileInputStream fis = new FileInputStream(".\\todo-app-plugin\\src\\main\\resources\\app.html");
        BufferedReader bf = new BufferedReader(new InputStreamReader(fis, "UTF-8"))){
        String rida = bf.readLine();
        while (rida != null) {
            template += rida;
            rida = bf.readLine();
        }
    }
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body;
        String existingItems = "";
        for (String taskInList:toDoList) {
            existingItems+="• " + taskInList + "<form enctype=\"text/plain\" action=\"/todoapp/delete\" method=\"post\">\n" +
                    "    <div class=\"delete\"> <button type=\"submit\">Done</button>\n" +
                    "    </div>\n" +
                    "</form><br>";
        }
        if(toDoList.size()==0)
            template = template.replace("$$EXISTING$$", "No tasks");
        String response = template.replace("$$EXISTING$$", existingItems);
        body = response.getBytes();
        return new Response(StatusCode.OK, responseHeaders, body);
    }

    @Mapping(URI="/todoapp/delete", method="POST")
    public Response deleteTask(Request request) throws UnsupportedEncodingException {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Location", "/todoapp/form");
        return new Response(StatusCode.FOUND, responseHeaders, null);
    }

    @Override
    public void initialize(ServerConfig sc) {    }
}
