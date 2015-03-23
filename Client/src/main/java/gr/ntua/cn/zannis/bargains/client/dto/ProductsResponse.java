package gr.ntua.cn.zannis.bargains.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.client.dto.pagination.Meta;
import gr.ntua.cn.zannis.bargains.client.entities.Product;

import java.util.List;

/**
 * The class that wraps the response given from a
 * {@link gr.ntua.cn.zannis.bargains.client.impl.SkroutzRestClient#getProductByShopUid(long, String)} request.
 * @author zannis <zannis.kal@gmail.com>
 */
public class ProductsResponse {
    private List<Product> products;
    private Product product;
    private Meta meta;

    @JsonCreator
    public ProductsResponse(@JsonProperty("products") List<Product> products,
                            @JsonProperty("product") Product product,
                            @JsonProperty("meta") Meta meta) {
        this.products = products;
        this.product = product;
        this.meta = meta;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
