package gr.ntua.cn.zannis.bargains.webapp.client.responses.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.webapp.client.responses.meta.Meta;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Sku;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class SkuResponse extends RestResponseImpl<Sku> {

    @JsonCreator
    public SkuResponse(@JsonProperty("sku") Sku sku,
                       @JsonProperty("skus") List<Sku> skus,
                       @JsonProperty("meta") Meta meta,
                       @JsonProperty("error") String errorMessage) {
        this.item = sku;
        this.items = skus;
        this.meta = meta;
        this.errorMessage = errorMessage;
    }
}
