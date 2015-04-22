package gr.ntua.cn.zannis.bargains.webapp.client.responses.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.webapp.client.responses.meta.Meta;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Product;

import java.util.List;

/**
 * The class that wraps the response given from a {@link Product} request.
 *
 * @author zannis <zannis.kal@gmail.com>
 */
public class ProductResponse extends RestResponseImpl<Product> {

    @JsonCreator
    public ProductResponse(@JsonProperty("products") List<Product> products,
                           @JsonProperty("product") Product product,
                           @JsonProperty("meta") Meta meta,
                           @JsonProperty("error") String errorMessage) {
        this.items = products;
        this.item = product;
        this.meta = meta;
        this.errorMessage = errorMessage;
    }
}
