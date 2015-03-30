package gr.ntua.cn.zannis.bargains.client.persistence.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import gr.ntua.cn.zannis.bargains.client.persistence.SkroutzEntity;

import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for a Stock Keeping Unit which practically is
 * a collection of products.
 * @author zannis <zannis.kal@gmail.com
 */
@JsonRootName("sku")
@Entity
@Table(name = "skus", schema = "public")
public class Sku extends SkroutzEntity {

    protected static final long serialVersionUID = -1L;

    private long id;
    private String ean;
    private String pn;
    private String name;
    private String displayName;
    private long categoryId;
    private String firstProductShopInfo;
    private String clickUrl;
    private float priceMax;
    private float priceMin;
    private float reviewScore;
    private int shopCount;
    private String plainSpecSummary;
    private long manufacturerId;
    private boolean future;
    private int reviewsCount;
    private boolean virtual;
    private Images images;
    private List<Product> products;
    private Category category;

    public Sku(@JsonProperty("id") long id,
               @JsonProperty("ean") String ean,
               @JsonProperty("pn") String pn,
               @JsonProperty("name") String name,
               @JsonProperty("display_name") String displayName,
               @JsonProperty("category_id") long categoryId,
               @JsonProperty("first_product_shop_info") String firstProductShopInfo,
               @JsonProperty("click_url") String clickUrl,
               @JsonProperty("price_max") float priceMax,
               @JsonProperty("price_min") float priceMin,
               @JsonProperty("reviewscore") float reviewScore,
               @JsonProperty("shop_count") int shopCount,
               @JsonProperty("plain_spec_summary") String plainSpecSummary,
               @JsonProperty("manufacturer_id") long manufacturerId,
               @JsonProperty("future") boolean future,
               @JsonProperty("reviews_count") int reviewsCount,
               @JsonProperty("virtual") boolean virtual,
               @JsonProperty("images") Images images) {
        super();
        this.skroutzId = id;
        this.ean = ean;
        this.pn = pn;
        this.name = name;
        this.displayName = displayName;
        this.categoryId = categoryId;
        this.firstProductShopInfo = firstProductShopInfo;
        this.clickUrl = clickUrl;
        this.priceMax = priceMax;
        this.priceMin = priceMin;
        this.reviewScore = reviewScore;
        this.shopCount = shopCount;
        this.plainSpecSummary = plainSpecSummary;
        this.manufacturerId = manufacturerId;
        this.future = future;
        this.reviewsCount = reviewsCount;
        this.virtual = virtual;
        this.images = images;
    }

    public Sku() {
    }

    @Id
    @GeneratedValue(generator = "SkuSequence")
    @SequenceGenerator(name = "SkuSequence", sequenceName = "sku_seq", allocationSize = 1)
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Transient
    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    @Transient
    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "display_name", nullable = false, length = 100)
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Transient
    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    @Transient
    public String getFirstProductShopInfo() {
        return firstProductShopInfo;
    }

    public void setFirstProductShopInfo(String firstProductShopInfo) {
        this.firstProductShopInfo = firstProductShopInfo;
    }

    @Column(name = "click_url", nullable = false, length = 100)
    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    @Column(name = "price_max", nullable = false, precision = 2)
    public float getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(float priceMax) {
        this.priceMax = priceMax;
    }

    @Column(name = "price_min", nullable = false, precision = 2)
    public float getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(float priceMin) {
        this.priceMin = priceMin;
    }

    @Transient
    public float getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(float reviewScore) {
        this.reviewScore = reviewScore;
    }

    @Transient
    public int getShopCount() {
        return shopCount;
    }

    public void setShopCount(int shopCount) {
        this.shopCount = shopCount;
    }

    @Transient
    public String getPlainSpecSummary() {
        return plainSpecSummary;
    }

    public void setPlainSpecSummary(String plainSpecSummary) {
        this.plainSpecSummary = plainSpecSummary;
    }

    @Transient
    public long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    @Transient
    public boolean isFuture() {
        return future;
    }

    public void setFuture(boolean future) {
        this.future = future;
    }

    @Transient
    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    @Transient
    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    @Transient
    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    @OneToMany(mappedBy = "sku")
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "skroutz_id", nullable = false, insertable = false, updatable = false)
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public static class Images {
        String main;
        List<String> alternatives;

        public Images(@JsonProperty("main") String main,
                      @JsonProperty("alternatives") List<String> alternatives) {
            this.main = main;
            this.alternatives = alternatives;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public List<String> getAlternatives() {
            return alternatives;
        }

        public void setAlternatives(List<String> alternatives) {
            this.alternatives = alternatives;
        }
    }
}
