package gr.ntua.cn.zannis.bargains.webapp.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * The persistent class for a Stock Keeping Unit which practically is
 * a collection of products.
 *
 * @author zannis <zannis.kal@gmail.com
 */
@JsonRootName("sku")
@JsonIgnoreProperties({"web_uri", "comparable"})
@Entity
@Table(name = "skus", schema = "public", catalog = "bargainhunt")
@NamedQueries({
        @NamedQuery(name = "Sku.findBySkroutzId", query = "select s from Sku s where s.skroutzId = :skroutzId"),
        @NamedQuery(name = "Sku.findAll", query = "select s from Sku s"),
        @NamedQuery(name = "Sku.findAllCheckedBy", query = "select s from Sku s where s.checkedAt >= :date"),
        @NamedQuery(name = "Sku.findAllByCategory", query = "select s from Sku s where s.categoryId = :categ_id"),
        @NamedQuery(name = "Sku.findCrawled", query = "select s from Sku s where s.categoryId = :categ_id and size(s.products) > 2")
})
public class Sku extends SkroutzEntity {

    protected static final long serialVersionUID = -1L;

    private int id;
    private String ean;
    private String pn;
    private String name;
    private String displayName;
    private int categoryId;
    private String firstProductShopInfo;
    private String clickUrl;
    private float priceMax;
    private float priceMin;
    private float reviewScore;
    private int shopCount;
    private String plainSpecSummary;
    private int manufacturerId;
    private boolean future;
    private int reviewsCount;
    private boolean virtual;
    private String image;
    private List<Product> products;
    private Category category;

    public Sku(@JsonProperty("id") int id,
               @JsonProperty("ean") String ean,
               @JsonProperty("pn") String pn,
               @JsonProperty("name") String name,
               @JsonProperty("display_name") String displayName,
               @JsonProperty("category_id") int categoryId,
               @JsonProperty("first_product_shop_info") String firstProductShopInfo,
               @JsonProperty("click_url") String clickUrl,
               @JsonProperty("price_max") float priceMax,
               @JsonProperty("price_min") float priceMin,
               @JsonProperty("reviewscore") float reviewScore,
               @JsonProperty("shop_count") int shopCount,
               @JsonProperty("plain_spec_summary") String plainSpecSummary,
               @JsonProperty("manufacturer_id") int manufacturerId,
               @JsonProperty("future") boolean future,
               @JsonProperty("reviews_count") int reviewsCount,
               @JsonProperty("virtual") boolean virtual,
               @JsonProperty("images") Images images) {
        super();
        this.skroutzId = id;
        this.ean = ean;
        this.pn = pn;
        this.name = StringEscapeUtils.unescapeJson(name);
        this.displayName = StringEscapeUtils.unescapeJson(displayName);
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
        this.image = images.main;
    }

    public Sku() {
    }

    @Override
    public <T extends SkroutzEntity> void updateFrom(T restEntity) {
        this.name = ((Sku) restEntity).getName();
        this.displayName = ((Sku) restEntity).getDisplayName();
        this.clickUrl = ((Sku) restEntity).getClickUrl();
        this.priceMax = ((Sku) restEntity).getPriceMax();
        this.priceMin = ((Sku) restEntity).getPriceMin();
        if (restEntity.getEtag() != null) {
            this.etag = restEntity.getEtag();
        }
    }

    @Id
    @GeneratedValue(generator = "SkuSequence")
    @SequenceGenerator(name = "SkuSequence", sequenceName = "sku_seq", allocationSize = 1)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @NotNull
    @Size(max = 300)
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @Size(max = 300)
    @Column(name = "display_name")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @NotNull
    @Column(name = "category_id")
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Transient
    public String getFirstProductShopInfo() {
        return firstProductShopInfo;
    }

    public void setFirstProductShopInfo(String firstProductShopInfo) {
        this.firstProductShopInfo = firstProductShopInfo;
    }

    @Size(max = 300)
    @Column(name = "click_url")
    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    @NotNull
    @Column(name = "price_max", precision = 2)
    public float getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(float priceMax) {
        this.priceMax = priceMax;
    }

    @NotNull
    @Column(name = "price_min", precision = 2)
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
    public int getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(int manufacturerId) {
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

    @Column(name = "image_url")
    @Size(max = 300)
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @OneToMany(mappedBy = "sku")
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "skroutz_id", insertable = false, updatable = false)
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public static class Images implements Serializable {
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
