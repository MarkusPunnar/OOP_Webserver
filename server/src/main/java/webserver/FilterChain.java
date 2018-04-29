package webserver;

import java.util.List;

public class FilterChain {

    private List<Filter> appliedFilters;
    private RequestHandler handler;
    private int lastCalled = -1;

    public FilterChain(List<Filter> appliedFilters, RequestHandler handler) {
        this.appliedFilters = appliedFilters;
        this.handler = handler;
    }

    public Response filter(Request request) throws Exception {
        Response response;
        if (lastCalled == appliedFilters.size() - 1) {
            response = handler.handle(request);
        }
        else {
            lastCalled++;
            response = appliedFilters.get(lastCalled).doFilter(request, this);
        }
        return response;
    }
}
