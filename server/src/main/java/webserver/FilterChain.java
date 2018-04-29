package webserver;

import java.util.List;

public class FilterChain {

    private List<Filter> appliedFilters;
    private RequestHandler handler;
    private Filter lastCalled = null;

    public FilterChain(List<Filter> appliedFilters) {
        this.appliedFilters = appliedFilters;
    }

    public Response filter(Request request) throws Exception {
        Response response = null;
        int index = appliedFilters.indexOf(lastCalled);
        if (index != appliedFilters.size() - 1) {
            lastCalled = appliedFilters.get(appliedFilters.indexOf(lastCalled) + 1);
            response = lastCalled.doFilter(request, this);
        }
        if (index == appliedFilters.size() - 1) {
            response = handler.handle(request);
        }
        return response;
    }

    public void setHandler(RequestHandler handler) {
        this.handler = handler;
    }
}
