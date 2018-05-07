package webserver;

import java.lang.reflect.Method;

public class HandlerInfo {

    private Method handlerMethod;
    private RequestHandler handler;

    public HandlerInfo(Method handlerMethod, RequestHandler handler) {
        this.handlerMethod = handlerMethod;
        this.handler = handler;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public RequestHandler getHandler() {
        return handler;
    }
}
