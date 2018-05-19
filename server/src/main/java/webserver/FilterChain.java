package webserver;

import java.util.List;

public class FilterChain {

    private List<Filter> appliedFilters;
    private HandlerInfo handlerInfo;
    private int lastCalled = -1;

    public FilterChain(List<Filter> appliedFilters, HandlerInfo handlerInfo) {
        this.appliedFilters = appliedFilters;
        this.handlerInfo = handlerInfo;
    }

    public Response filter(Request request) throws Exception {
        Response response;
        if (lastCalled == appliedFilters.size() - 1) {
            response = (Response) handlerInfo.getHandlerMethod().invoke(handlerInfo.getHandler(), request);
        } else {
            lastCalled++;
            response = appliedFilters.get(lastCalled).doFilter(request, this);
        }
        return response;
    }
}
