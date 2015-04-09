package gr.ntua.cn.zannis.bargains.client.dto.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Meta;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Category;

import java.util.List;

/**
 * Simple wrapper class for search results.
 * @author zannis <zannis.kal@gmail.com>
 */
public class SearchResults extends CategoryResponse {

    public SearchResults(@JsonProperty("category") Category category,
                         @JsonProperty("categories") List<Category> categories,
                         @JsonProperty("meta") Meta meta) {
        super(category, categories, meta);

    }

    public List<Category> getCategories() {
        return items;
    }

    public Meta.StrongMatches getStrongMatches() {
        return meta.getStrongMatches();
    }

    public List<Meta.Alternative> getAlternatives() {
        return meta.getAlternatives();
    }
}