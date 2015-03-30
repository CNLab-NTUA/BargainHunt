package gr.ntua.cn.zannis.bargains.client.dto.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Meta;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Page;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Sku;

import java.net.URI;
import java.util.List;
import java.util.Map;

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

    @Override
    public Sku getItem() {
        return item;
    }

    @Override
    public List<Sku> getItems() {
        return items;
    }

    @Override
    public Page<Sku> getPage() {
        return null;
    }

    @Override
    public Meta getMeta() {
        return meta;
    }

    @Override
    public Map<String, URI> getLinks() {
        return links;
    }
}
