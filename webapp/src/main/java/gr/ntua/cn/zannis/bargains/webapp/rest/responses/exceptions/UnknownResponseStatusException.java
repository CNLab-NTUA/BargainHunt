package gr.ntua.cn.zannis.bargains.webapp.rest.responses.exceptions;

import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;

import javax.ws.rs.core.Response;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class UnknownResponseStatusException extends RuntimeException {

    private Response.StatusType status;

    public <T extends SkroutzEntity> UnknownResponseStatusException(Class<T> tClass, Integer skroutzId, Response.StatusType status) {
        super("The " + tClass.getSimpleName() + " with skroutzId " + skroutzId + " returned status " + status.getStatusCode());
        this.status = status;
    }

    public Response.StatusType getStatus() {
        return status;
    }
}
