package gr.ntua.cn.zannis.bargains.client.dto.impl;

import gr.ntua.cn.zannis.bargains.client.dto.RestResponse;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Meta;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Page;
import gr.ntua.cn.zannis.bargains.client.persistence.PersistentEntity;

import javax.ws.rs.core.Link;
import java.util.List;
import java.util.Map;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class RestResponseImpl<T extends PersistentEntity> implements RestResponse<T> {
    protected Map<String, Link> links;
    protected List<T> items;
    protected T item;
    protected Meta meta;

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

    public Map<String, Link> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Link> links) {
        this.links = links;
    }
}
