package webserver;

import java.util.ArrayList;
import java.util.List;

public class FilterChain {

    private List<Filter> appliedFilters = new ArrayList<>();
    private Response response;
    private Request request;

    public void filter() throws Exception {
        for (Filter filter: appliedFilters) {
            filter.doFilter(request);
        }
    }

    public void createFilterInstances() {
        appliedFilters.add(new LoginFilter());
    }
}
