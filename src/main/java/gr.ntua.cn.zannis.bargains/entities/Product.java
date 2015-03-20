package gr.ntua.cn.zannis.bargains.entities;

/**
 * @author zannis <zannis.kal@gmail.com
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("product")
public class Product {

    private long id;
    private long skroutzId;
    private String name;
    private long skuId;
    private int shopId;
    private int categoryId;
    private String etag;
    private String availability;
    private String clickUrl;
    private String shopUid;
    private double price;

    public Product() {
    }

    @JsonCreator
    public Product(@JsonProperty("id") long skroutzId,
                   @JsonProperty("name") String name,
                   @JsonProperty("sku_id") long skuId,
                   @JsonProperty("shop_id") int shopId,
                   @JsonProperty("category_id") int categoryId,
                   @JsonProperty("availability") String availability,
                   @JsonProperty("click_url") String clickUrl,
                   @JsonProperty("shop_uid") String shopUid,
                   @JsonProperty("price") double price) {
        this.skroutzId = skroutzId;
        this.name = name;
        this.skuId = skuId;
        this.shopId = shopId;
        this.categoryId = categoryId;
        this.availability = availability;
        this.clickUrl = clickUrl;
        this.shopUid = shopUid;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    public String getShopUid() {
        return shopUid;
    }

    public void setShopUid(String shopUid) {
        this.shopUid = shopUid;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @JsonIgnore
    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public long getSkroutzId() {
        return skroutzId;
    }

    public void setSkroutzId(long skroutzId) {
        this.skroutzId = skroutzId;
    }
}
