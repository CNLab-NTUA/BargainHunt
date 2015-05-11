package gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta;

import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;

import java.net.URI;
import java.util.List;

/**
 * Custom wrapper class to handle paginated results in API requests.
 *
 * @author zannis <zannis.kal@gmail.com>
 */
public class Page<T extends SkroutzEntity> extends Pagination {
    private Class<T> entityType;
    private List<T> items;
    private URI prev;
    private URI next;
    private URI last;
    private URI uri;

    @SuppressWarnings("unchecked")
    public Page(List<T> items, Pagination pagination, URI prev, URI next, URI last) {
        if (items != null && !items.isEmpty()) {
            this.entityType = (Class<T>) items.get(0).getClass();
        } else {
            this.entityType = null;
        }
        this.items = items;
        this.prev = prev;
        this.next = next;
        this.last = last;
        this.per = pagination.getPer();
        this.currentPage = pagination.getCurrentPage();
        this.totalPages = pagination.getTotalPages();
        this.totalResults = pagination.getTotalResults();
    }

    public T getFirstItem() {
        return !items.isEmpty() ? items.get(0) : null;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public URI getPrev() {
        return prev;
    }

    public void setPrev(URI prev) {
        this.prev = prev;
    }

    public URI getNext() {
        return next;
    }

    public void setNext(URI next) {
        this.next = next;
    }

    public URI getLast() {
        return last;
    }

    public void setLast(URI last) {
        this.last = last;
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
