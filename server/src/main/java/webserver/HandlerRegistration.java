package webserver;

import java.lang.reflect.Method;
import java.util.Map;

public class HandlerRegistration {

    public void register(RequestHandler handler, Map<MappingInfo, HandlerInfo> map) {
        Class<?> objectClass = handler.getClass();
        for (Method method : objectClass.getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(Mapping.class)) {
                String URIvalue = method.getAnnotation(Mapping.class).URI();
                String requestMethod = method.getAnnotation(Mapping.class).method().toUpperCase();
                MappingInfo mappingInfo = new MappingInfo(URIvalue, requestMethod);
                HandlerInfo handlerInfo = new HandlerInfo(method, handler);
                map.put(mappingInfo, handlerInfo);
            }
        }
    }
}
