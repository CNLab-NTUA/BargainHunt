package gr.ntua.cn.zannis.bargains.webapp.rest.responses.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Shop;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta.Meta;

import java.util.List;

/**
 * The class that wraps the JSON entity responses from shop related requests.
 *
 * @author zannis <zannis.kal@gmail.com>
 */
public class ShopResponse extends RestResponseImpl<Shop> {

    public ShopResponse(@JsonProperty("shop") Shop shop,
                        @JsonProperty("shops") List<Shop> shops,
//                        @JsonProperty("reviews") List<Shop.Review> reviews,
//                        @JsonProperty("locations") List<Shop.Location> locations,
//                        @JsonProperty("location") Shop.Location location,
                        @JsonProperty("meta") Meta meta,
                        @JsonProperty("error") String errorMessage) {
        this.item = shop;
        this.items = shops;
//        this.reviews = reviews;
//        this.locations = locations;
//        this.location = location;
        this.meta = meta;
        this.errorMessage = errorMessage;
    }
}
