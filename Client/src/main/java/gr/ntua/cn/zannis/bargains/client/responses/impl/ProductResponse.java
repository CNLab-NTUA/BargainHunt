package gr.ntua.cn.zannis.bargains.client.responses.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.client.responses.meta.Meta;

import java.util.List;

/**
 * The class that wraps the response given from a {@link Product} request.
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
