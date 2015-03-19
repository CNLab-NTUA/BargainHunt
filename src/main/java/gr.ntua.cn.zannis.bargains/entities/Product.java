package gr.ntua.cn.zannis.bargains.entities;

/**
 * @author zannis <zannis.kal@gmail.com
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {

    private long id;
    private String name;
    private long skuId;
    private long shopId;
    private int categoryId;
    private String availability;
    private String clickUrl;
    private int shopUid;
    private double price;

    public Product() {
    }

    @JsonCreator
    public Product(long id, String name, long skuId, long shopId, int categoryId, String availability, String clickUrl, int shopUid, double price) {
        this.id = id;
        this.name = name;
        this.skuId = skuId;
        this.shopId = shopId;
        this.categoryId = categoryId;
        this.availability = availability;
        this.clickUrl = clickUrl;
        this.shopUid = shopUid;
        this.price = price;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("sku_id")
    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    @JsonProperty("shop_id")
    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    @JsonProperty("category_id")
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @JsonProperty
    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @JsonProperty("click_url")
    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    @JsonProperty("shop_uid")
    public int getShopUid() {
        return shopUid;
    }

    public void setShopUid(int shopUid) {
        this.shopUid = shopUid;
    }

    @JsonProperty
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
