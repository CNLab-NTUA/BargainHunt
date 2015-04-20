package gr.ntua.cn.zannis.bargains.client.responses.meta;

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
    protected int currentPage;
    protected int per;

    public Pagination() {
    }

    @JsonCreator
    public Pagination(@JsonProperty("total_results") int totalResults,
                      @JsonProperty("total_pages") int totalPages,
                      @JsonProperty("page") int currentPage,
                      @JsonProperty("per") int per) {
        this.totalResults = totalResults;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
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

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPer() {
        return per;
    }

    public void setPer(int per) {
        this.per = per;
    }

    public boolean isFirstPage() {
        return currentPage == 1 || totalPages == 1;
    }

    public boolean isMiddlePage() {
        return (currentPage != 1 && currentPage != totalPages);
    }

    public boolean isLastPage() {
        return (currentPage == totalPages);
    }

    public boolean hasNext() {
        return (currentPage < totalPages);
    }
}