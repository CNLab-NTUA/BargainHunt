package gr.ntua.cn.zannis.bargains.client.dto.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.*;

import java.util.List;

/**
 * Class that represents the "meta" tag in JSON responses and its contents.
 * @author zannis <zannis.kal@gmail.com>
 */
@JsonRootName("meta")
public class Meta {
    private String query;
    private List<Alternative> alternatives;
    private StrongMatches strongMatches;
    private Pagination pagination;

    @JsonCreator
    public Meta(@JsonProperty("q") String query,
                @JsonProperty("alternatives") List<Alternative> alternatives,
                @JsonProperty("strongMatches") StrongMatches strongMatches,
                @JsonProperty("pagination") Pagination pagination) {
        this.query = query;
        this.alternatives = alternatives;
        this.strongMatches = strongMatches;
        this.pagination = pagination;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    public StrongMatches getStrongMatches() {
        return strongMatches;
    }

    public void setStrongMatches(StrongMatches strongMatches) {
        this.strongMatches = strongMatches;
    }

    private static class Alternative {
        private String term;
        private boolean important;

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public boolean isImportant() {
            return important;
        }

        public void setImportant(boolean important) {
            this.important = important;
        }
    }

    private static class StrongMatches {
        private Product product;
        private Category category;
        private Sku sku;
        private Shop shop;
        private Manufacturer manufacturer;

        public StrongMatches(@JsonProperty("product") Product product,
                             @JsonProperty("category") Category category,
                             @JsonProperty("sku") Sku sku,
                             @JsonProperty("shop") Shop shop,
                             @JsonProperty("manufacturer") Manufacturer manufacturer) {
            this.product = product;
            this.category = category;
            this.sku = sku;
            this.shop = shop;
            this.manufacturer = manufacturer;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public Sku getSku() {
            return sku;
        }

        public void setSku(Sku sku) {
            this.sku = sku;
        }

        public Shop getShop() {
            return shop;
        }

        public void setShop(Shop shop) {
            this.shop = shop;
        }

        public Manufacturer getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(Manufacturer manufacturer) {
            this.manufacturer = manufacturer;
        }
    }
}
