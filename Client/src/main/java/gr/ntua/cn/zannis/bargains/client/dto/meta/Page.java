package gr.ntua.cn.zannis.bargains.client.dto.meta;

import javax.ws.rs.core.Link;
import java.util.List;

/**
 * Custom wrapper class to handle paginated results in API requests.
 * @author zannis <zannis.kal@gmail.com>
 */
public class Page<T> extends Pagination {
    private List<T> items;
    private Link prev;
    private Link next;
    private Link last;

    public Page(List<T> items, Pagination pagination, Link prev, Link next, Link last) {
        this.items = items;
        this.prev = prev;
        this.next = next;
        this.last = last;
        this.per = pagination.getPer();
        this.page = pagination.getPage();
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

    public Link getPrev() {
        return prev;
    }

    public void setPrev(Link prev) {
        this.prev = prev;
    }

    public Link getNext() {
        return next;
    }

    public void setNext(Link next) {
        this.next = next;
    }

    public Link getLast() {
        return last;
    }

    public void setLast(Link last) {
        this.last = last;
    }
}
