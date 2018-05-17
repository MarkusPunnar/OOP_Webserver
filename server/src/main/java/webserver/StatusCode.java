package webserver;

public enum StatusCode {

    OK(200),
    CREATED(201),
    FOUND(302),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    NOT_ALLOWED(405),
    INTERNAL_ERROR(500);

    private final int code;
    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
