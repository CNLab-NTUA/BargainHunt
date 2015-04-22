package gr.ntua.cn.zannis.bargains.webapp.client.responses.impl;

import gr.ntua.cn.zannis.bargains.webapp.client.responses.RestResponse;
import gr.ntua.cn.zannis.bargains.webapp.client.responses.meta.Meta;
import gr.ntua.cn.zannis.bargains.webapp.client.responses.meta.Page;
import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class RestResponseImpl<T extends SkroutzEntity> implements RestResponse<T> {
    protected Map<String, URI> links;
    protected List<T> items;
    protected T item;
    protected Meta meta;
    protected String errorMessage;

    @Override
    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    @Override
    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    @Override
    public Page<T> getPage() {
        if (links == null) {
            return new Page<>(items, meta.getPagination(), null, null, null);
        } else {
            return new Page<>(items, meta.getPagination(),
                    links.get("prev"), links.get("next"), links.get("last"));
        }
    }

    @Override
    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    @Override
    public Map<String, URI> getLinks() {
        return links;
    }

    public void setLinks(Map<String, URI> links) {
        this.links = links;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
