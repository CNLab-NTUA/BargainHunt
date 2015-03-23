package gr.ntua.cn.zannis.bargains.client.dto.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Class that represents the "meta" tag in JSON responses and its contents.
 * @author zannis <zannis.kal@gmail.com>
 */
@JsonRootName("meta")
public class Meta {
    private Pagination pagination;

    @JsonCreator
    public Meta(@JsonProperty("pagination") Pagination pagination) {
        this.pagination = pagination;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    /**
     * Class that represents the pagination JSON field.
     */
    @JsonRootName("pagination")
    static class Pagination {
        int totalResults;
        int totalPages;
        int page;
        int per;

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
    }
}
