package gr.ntua.cn.zannis.bargains.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Meta;
import gr.ntua.cn.zannis.bargains.client.entities.Shop;

import java.util.List;

/**
 * The class that wraps the JSON entity responses from shop related requests.
 * @author zannis <zannis.kal@gmail.com>
 */
public class ShopsResponse {
    private Shop shop;
    private List<Shop> shops;
    private List<Shop.Review> reviews;
    private List<Shop.Location> locations;
    private Shop.Location location;
    private Meta meta;

    public ShopsResponse(@JsonProperty("shop") Shop shop,
                         @JsonProperty("shops") List<Shop> shops,
                         @JsonProperty("reviews") List<Shop.Review> reviews,
                         @JsonProperty("locations") List<Shop.Location> locations,
                         @JsonProperty("location") Shop.Location location,
                         @JsonProperty("meta") Meta meta) {
        this.shop = shop;
        this.shops = shops;
        this.reviews = reviews;
        this.locations = locations;
        this.location = location;
        this.meta = meta;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<Shop> getShops() {
        return shops;
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }

    public List<Shop.Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Shop.Review> reviews) {
        this.reviews = reviews;
    }

    public List<Shop.Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Shop.Location> locations) {
        this.locations = locations;
    }

    public Shop.Location getLocation() {
        return location;
    }

    public void setLocation(Shop.Location location) {
        this.location = location;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
