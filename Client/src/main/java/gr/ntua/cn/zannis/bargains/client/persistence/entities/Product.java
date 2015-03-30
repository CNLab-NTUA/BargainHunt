package gr.ntua.cn.zannis.bargains.client.persistence.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import gr.ntua.cn.zannis.bargains.client.persistence.SkroutzEntity;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * The Product class that contains all the information we get from the Skroutz API
 * including the persistent fields such as inserted, checked, and modified dates.
 *
 * @author zannis <zannis.kal@gmail.com
 */
@JsonRootName("product")
@Entity
@Table(name = "products", schema = "public")
public class Product extends SkroutzEntity {

    private static final long serialVersionUID = -1L;

    private long id;
    private String name;
    private long skuId;
    private int shopId;
    private int categoryId;
    private int priceChanges;
    private float averagePastPrice;
    private String availability;
    private String clickUrl;
    private String shopUid;
    private float price;
    private boolean bargain;
    private Sku sku;
    private Shop shop;
    private Category category;
    private List<Price> prices;

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
        super();
        this.skroutzId = skroutzId;
        this.name = StringEscapeUtils.unescapeJson(name);
        this.skuId = skuId;
        this.shopId = shopId;
        this.categoryId = categoryId;
        this.availability = StringEscapeUtils.unescapeJson(availability);
        this.clickUrl = clickUrl;
        this.shopUid = shopUid;
        this.price = price;
    }

    public Product() {
    }

    @Id
    @GeneratedValue(generator = "ProductSequence")
    @SequenceGenerator(name = "ProductSequence", sequenceName = "prod_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false, insertable = false, updatable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    @Transient
    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    @Transient
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name = "availability", nullable = true, insertable = false, updatable = false, length = 50)
    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @Column(name = "click_url", nullable = true, insertable = false, updatable = false, length = 200)
    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    @Column(name = "shop_uid", nullable = false, insertable = false, updatable = false, length = 30)
    public String getShopUid() {
        return shopUid;
    }

    public void setShopUid(String shopUid) {
        this.shopUid = shopUid;
    }

    @Column(name = "price", nullable = true, insertable = false, updatable = false, precision = 2)
    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Column(name = "price_changes", nullable = false, insertable = false, updatable = false)
    public int getPriceChanges() {
        return priceChanges;
    }

    public void setPriceChanges(int priceChanges) {
        this.priceChanges = priceChanges;
    }

    @Column(name = "average_past_price", nullable = false, insertable = false, updatable = false, precision = 2)
    public float getAveragePastPrice() {
        return averagePastPrice;
    }

    public void setAveragePastPrice(float averagePastPrice) {
        this.averagePastPrice = averagePastPrice;
    }

    @Column(name = "is_bargain", nullable = false, insertable = false, updatable = false)
    public boolean isBargain() {
        return bargain;
    }

    public void setBargain(boolean bargain) {
        this.bargain = bargain;
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
        if (eTag != null) {
            this.etag = eTag;
        }
        return this;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", checkedAt=" + checkedAt +
                ", availability=" + availability +
                ", skroutzId=" + skroutzId +
                '}';
    }

    @OneToMany(mappedBy = "product")
    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

    @ManyToOne
    @JoinColumn(name = "sku_id", referencedColumnName = "skroutz_id", nullable = false, insertable = false, updatable = false)
    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    @ManyToOne
    @JoinColumn(name = "shop_id", referencedColumnName = "skroutz_id", nullable = false, insertable = false, updatable = false)
    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "skroutz_id", nullable = false, insertable = false, updatable = false)
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) &&
                Objects.equals(skuId, product.skuId) &&
                Objects.equals(shopId, product.shopId) &&
                Objects.equals(categoryId, product.categoryId) &&
                Objects.equals(priceChanges, product.priceChanges) &&
                Objects.equals(averagePastPrice, product.averagePastPrice) &&
                Objects.equals(price, product.price) &&
                Objects.equals(bargain, product.bargain) &&
                Objects.equals(name, product.name) &&
                Objects.equals(availability, product.availability) &&
                Objects.equals(clickUrl, product.clickUrl) &&
                Objects.equals(shopUid, product.shopUid) &&
                Objects.equals(sku, product.sku) &&
                Objects.equals(shop, product.shop) &&
                Objects.equals(category, product.category) &&
                Objects.equals(prices, product.prices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, skuId, shopId, categoryId, priceChanges, averagePastPrice, availability, clickUrl, shopUid, price, bargain, sku, shop, category, prices);
    }
}
