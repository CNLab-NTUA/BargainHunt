package gr.ntua.cn.zannis.bargains.webapp.rest.responses.exceptions;

import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class ResponseWithoutEntityException extends RuntimeException {

    public <T extends SkroutzEntity> ResponseWithoutEntityException(Class<T> tClass, Integer skroutzId) {
        super("The " + tClass.getSimpleName() + " with skroutzId " + skroutzId + " has no content.");
    }
}
