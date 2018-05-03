package webserver;

import java.lang.reflect.Method;
import java.util.Map;

public class HandlerRegistration {

    public Map<MappingInfo, RequestHandler> register(RequestHandler handler, Map<MappingInfo, RequestHandler> map) {
        Class<?> objectClass = handler.getClass();
        for (Method method : objectClass.getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(Mapping.class)) {
                String URIvalue = method.getAnnotation(Mapping.class).URI();
                String requestMethod = method.getAnnotation(Mapping.class).method();
                MappingInfo handlerInfo = new MappingInfo(URIvalue, requestMethod);
                map.put(handlerInfo, handler);
            }
        }
        return map;
    }
}
