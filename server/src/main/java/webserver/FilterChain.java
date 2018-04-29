package webserver;

import java.util.ArrayList;
import java.util.List;

public class FilterChain {

    private List<Filter> appliedFilters = new ArrayList<>();
    private RequestHandler handler;

    public Response filter(Request request, Filter lastCalled) throws Exception {
        Response response = null;
        Filter toCall;
        int index = appliedFilters.indexOf(lastCalled);
        if (index != appliedFilters.size() - 1) {
            if (lastCalled == null) {
                toCall = appliedFilters.get(0);
            } else {
                toCall = appliedFilters.get(appliedFilters.indexOf(lastCalled) + 1);
            }
            response = toCall.doFilter(request, this);
        }
        if (response == null) {
            response = handler.handle(request);
        }
        return response;
    }

    public void createFilterInstances() {
        appliedFilters.add(new LoginFilter());
    }

    public List<Filter> getAppliedFilters() {
        return appliedFilters;
    }

    public void setHandler(RequestHandler handler) {
        this.handler = handler;
    }
}
