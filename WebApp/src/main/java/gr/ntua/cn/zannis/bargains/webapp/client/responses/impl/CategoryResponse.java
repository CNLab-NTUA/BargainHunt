package gr.ntua.cn.zannis.bargains.webapp.client.responses.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.webapp.client.responses.meta.Meta;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Category;

import java.util.List;

/**
 * The wrapper class for {@link Category} related
 * requests.
 *
 * @author zannis <zannis.kal@gmail.com
 */
public class CategoryResponse extends RestResponseImpl<Category> {

    public CategoryResponse(@JsonProperty("category") Category category,
                            @JsonProperty("categories") List<Category> categories,
                            @JsonProperty("meta") Meta meta,
                            @JsonProperty("error") String errorMessage) {
        this.item = category;
        this.items = categories;
        this.meta = meta;
        this.errorMessage = errorMessage;
    }
}
