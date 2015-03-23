package gr.ntua.cn.zannis.bargains.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Meta;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Page;
import gr.ntua.cn.zannis.bargains.client.entities.Product;

import javax.ws.rs.core.Link;
import java.util.List;
import java.util.Map;

/**
 * The class that wraps the response given from a
 * {@link gr.ntua.cn.zannis.bargains.client.impl.SkroutzRestClient#getProductByShopUid(long, String)} request.
 * @author zannis <zannis.kal@gmail.com>
 */
public class ProductResponse {
    private List<Product> products;
    private Product product;
    private Meta meta;

    @JsonCreator
    public ProductResponse(@JsonProperty("products") List<Product> products,
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

    public Page<Product> toPage(Map<String, Link> links) {
        return new Page<>(products, meta.getPagination(), links.get("prev"), links.get("next"), links.get("last"));
    }
}
