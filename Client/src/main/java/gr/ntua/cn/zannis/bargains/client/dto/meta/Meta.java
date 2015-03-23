package gr.ntua.cn.zannis.bargains.client.dto.meta;

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

}
