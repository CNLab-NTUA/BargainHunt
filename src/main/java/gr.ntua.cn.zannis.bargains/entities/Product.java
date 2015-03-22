package gr.ntua.cn.zannis.bargains.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Date;

/**
 * The Product class that contains all the information we get from the Skroutz API
 * including the persistent fields such as inserted, checked, and modified dates.
 *
 * @author zannis <zannis.kal@gmail.com
 */
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
    private float price;
    private Date insertedAt;
    private Date checkedAt;
    private Date modifiedAt;

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
                   @JsonProperty("price") float price) {
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

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

    public Date getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Date insertedAt) {
        this.insertedAt = insertedAt;
    }

    public Date getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(Date checkedAt) {
        this.checkedAt = checkedAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", checkedAt=" + checkedAt +
                ", skroutzId=" + skroutzId +
                '}';
    }

    /**
     * Update our persistent product entity with information from the API service.
     * @param product The product entity containing the updated information.
     * @param eTag The Etag taken from the HTTP Response headers.
     * @return The updated entity.
     */
    public Product updateFromJson(Product product, String eTag) {
        Date now = new Date();
        this.name = product.name;
        this.skuId = product.skuId;
        this.availability = product.availability;
        this.clickUrl = product.clickUrl;
        this.shopUid = product.shopUid;
        this.price = product.price;
        this.checkedAt = now;
        this.modifiedAt = now;
        this.etag = eTag;
        return this;
    }

    public void wasJustChecked() {
        this.checkedAt = new Date();
    }
}
