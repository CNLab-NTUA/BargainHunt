package gr.ntua.cn.zannis.bargains.webapp.rest.responses.impl;

import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class UriResponse {
    private Response response;
    private URI uri;

    public UriResponse(Response response, URI uri) {
        this.response = response;
        this.uri = uri;
    }

    public UriResponse() {
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
