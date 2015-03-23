package gr.ntua.cn.zannis.bargains.client.components;

import gr.ntua.cn.zannis.bargains.client.dto.meta.Pagination;

import java.net.URI;
import java.util.List;

/**
 * Custom wrapper class to handle paginated results in API requests.
 * @author zannis <zannis.kal@gmail.com>
 */
public class Page<T> extends Pagination {
    private List<T> entities;
    private URI prevPage;
    private URI nextPage;
    private URI lastPage;

    public Page(List<T> entities, Pagination pagination, URI prevPage, URI nextPage, URI lastPage) {
        this.entities = entities;
        this.prevPage = prevPage;
        this.nextPage = nextPage;
        this.lastPage = lastPage;
        this.per = pagination.getPer();
        this.page = pagination.getPage();
        this.totalPages = pagination.getTotalPages();
        this.totalResults = pagination.getTotalResults();
    }

    public List<T> getEntities() {
        return entities;
    }

    public void setEntities(List<T> entities) {
        this.entities = entities;
    }

    public URI getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(URI prevPage) {
        this.prevPage = prevPage;
    }

    public URI getNextPage() {
        return nextPage;
    }

    public void setNextPage(URI nextPage) {
        this.nextPage = nextPage;
    }

    public URI getLastPage() {
        return lastPage;
    }

    public void setLastPage(URI lastPage) {
        this.lastPage = lastPage;
    }
}
