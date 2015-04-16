package gr.ntua.cn.zannis.bargains.client.exceptions;

import javax.ws.rs.core.Response;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class UnexpectedResponseStatusException extends RuntimeException {
    public UnexpectedResponseStatusException(Response.StatusType info) {
        super(info.getReasonPhrase());
    }
}
