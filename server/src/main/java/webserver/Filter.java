package webserver;

public interface Filter {

    Response doFilter(Request request, FilterChain chain) throws Exception;

}
