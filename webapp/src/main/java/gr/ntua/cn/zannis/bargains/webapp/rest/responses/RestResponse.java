package gr.ntua.cn.zannis.bargains.webapp.rest.responses;

import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta.Meta;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta.Page;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public interface RestResponse<T extends SkroutzEntity> {
    T getItem();

    Map<String, URI> getLinks();

    List<T> getItems();

    Page<T> getPage();

    Meta getMeta();

    String getErrorMessage();

    URI getUri();
}