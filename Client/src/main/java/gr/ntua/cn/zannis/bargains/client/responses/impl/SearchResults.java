package gr.ntua.cn.zannis.bargains.client.responses.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.client.responses.meta.Meta;

import java.util.List;

/**
 * Simple wrapper class for search results.
 * @author zannis <zannis.kal@gmail.com>
 */
public class SearchResults extends CategoryResponse {

    public SearchResults(@JsonProperty("category") Category category,
                         @JsonProperty("categories") List<Category> categories,
                         @JsonProperty("meta") Meta meta,
                         @JsonProperty("error") String errorMessage) {
        super(category, categories, meta, errorMessage);

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

    public boolean hasStrongMatches() {
        return getStrongMatches() != null;
    }

}
