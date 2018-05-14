package webserver;

public class Part {
    private String name;
    private byte[] body;

    public Part(String name, byte[] body) {
        this.name = name;
        this.body = body;
    }

    public String toString(){
        try{
            return body.toString();
        }
        catch (Exception e){
            return "";
        }
    }
}
