package gr.ntua.cn.zannis.bargains.client.responses.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Shop;
import gr.ntua.cn.zannis.bargains.client.responses.meta.Meta;

import java.util.List;

/**
 * The class that wraps the JSON entity responses from shop related requests.
 * @author zannis <zannis.kal@gmail.com>
 */
public class ShopResponse extends RestResponseImpl<Shop> {

    public ShopResponse(@JsonProperty("shop") Shop shop,
                        @JsonProperty("shops") List<Shop> shops,
//                        @JsonProperty("reviews") List<Shop.Review> reviews,
//                        @JsonProperty("locations") List<Shop.Location> locations,
//                        @JsonProperty("location") Shop.Location location,
                        @JsonProperty("meta") Meta meta) {
        this.item = shop;
        this.items = shops;
//        this.reviews = reviews;
//        this.locations = locations;
//        this.location = location;
        this.meta = meta;
    }
}
