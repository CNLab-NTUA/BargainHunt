package gr.ntua.cn.zannis.bargains.client.responses.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Sku;
import gr.ntua.cn.zannis.bargains.client.responses.meta.Meta;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class SkuResponse extends RestResponseImpl<Sku> {

    @JsonCreator
    public SkuResponse(@JsonProperty("sku") Sku sku,
                       @JsonProperty("skus") List<Sku> skus,
                       @JsonProperty("meta") Meta meta) {
        this.item = sku;
        this.items = skus;
        this.meta = meta;
    }
}
