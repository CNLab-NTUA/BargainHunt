package gr.ntua.cn.zannis.bargains.client.dto.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Meta;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Product;

import java.util.List;

/**
 * The class that wraps the response given from a
 * {@link gr.ntua.cn.zannis.bargains.client.impl.SkroutzRestClient#getProductByShopUid(long, String)} request.
 * @author zannis <zannis.kal@gmail.com>
 */
public class ProductResponse extends RestResponseImpl<Product> {

    @JsonCreator
    public ProductResponse(@JsonProperty("products") List<Product> products,
                           @JsonProperty("product") Product product,
                           @JsonProperty("meta") Meta meta) {
        this.items = products;
        this.item = product;
        this.meta = meta;
    }
}
