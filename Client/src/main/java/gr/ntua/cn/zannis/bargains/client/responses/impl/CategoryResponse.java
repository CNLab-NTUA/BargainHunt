package gr.ntua.cn.zannis.bargains.client.responses.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.client.responses.meta.Meta;

import java.util.List;

/**
 * The wrapper class for {@link Category} related
 * requests.
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
