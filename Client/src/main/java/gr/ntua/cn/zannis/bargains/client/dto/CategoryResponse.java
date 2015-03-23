package gr.ntua.cn.zannis.bargains.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Meta;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Page;
import gr.ntua.cn.zannis.bargains.client.entities.Category;

import javax.ws.rs.core.Link;
import java.util.List;
import java.util.Map;

/**
 * The wrapper class for {@link gr.ntua.cn.zannis.bargains.client.entities.Category} related
 * requests.
 * @author zannis <zannis.kal@gmail.com
 */
public class CategoryResponse {
    private Category category;
    private List<Category> categories;
    private Meta meta;

    public CategoryResponse(@JsonProperty("category") Category category,
                            @JsonProperty("categories") List<Category> categories,
                            @JsonProperty("meta") Meta meta) {
        this.category = category;
        this.categories = categories;
        this.meta = meta;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Page<Category> toPage(Map<String, Link> links) {
        return new Page<>(categories, meta.getPagination(), links.get("prev"), links.get("next"), links.get("last"));
    }
}
