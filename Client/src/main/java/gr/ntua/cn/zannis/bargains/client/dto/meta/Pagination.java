package gr.ntua.cn.zannis.bargains.client.dto.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Class that represents the pagination JSON field.
 */
@JsonRootName("pagination")
public class Pagination {
    protected int totalResults;
    protected int totalPages;
    protected int page;
    protected int per;

    public Pagination() {
    }

    @JsonCreator
    public Pagination(@JsonProperty("total_results") int totalResults,
                      @JsonProperty("total_pages") int totalPages,
                      @JsonProperty("page") int page,
                      @JsonProperty("per") int per) {
        this.totalResults = totalResults;
        this.totalPages = totalPages;
        this.page = page;
        this.per = per;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPer() {
        return per;
    }

    public void setPer(int per) {
        this.per = per;
    }

    public boolean isFirstPage() {
        return page == 1 || totalPages == 1;
    }

    public boolean isMiddlePage() {
        return (page != 1 && page != totalPages);
    }

    public boolean isLastPage() {
        return (page == totalPages);
    }

    public boolean hasNext() {
        return (page < totalPages);
    }
}