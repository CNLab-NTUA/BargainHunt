package gr.ntua.cn.zannis.bargains.webapp.persistence.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Product class that contains all the information we get from the Skroutz API
 * including the persistent fields such as inserted, checked, and modified dates.
 *
 * @author zannis <zannis.kal@gmail.com
 */
@JsonRootName("product")
@JsonIgnoreProperties({"web_uri", "sizes", "immediate_pickup"})
@Entity
@Table(name = "products", schema = "public", catalog = "bargainhunt")
@NamedQueries({
        @NamedQuery(name = "Product.findAll", query = "select p from Product p"),
        @NamedQuery(name = "Product.findBySkroutzId", query = "select p from Product p where p.skroutzId = :skroutzId"),
        @NamedQuery(name = "Product.findAllByShop", query = "select p from Product p where p.shop = :shop"),
        @NamedQuery(name = "Product.findAllBySku", query = "select p from Product p where p.sku = :sku")
})
public class Product extends SkroutzEntity {

    private static final long serialVersionUID = -1L;

    private int id;
    private String name;
    private int skuId;
    private int shopId;
    private int categoryId;
    private int priceChanges;
    private float averagePastPrice;
    private String availability;
    private String clickUrl;
    private String shopUid;
    private float price;
    private String expenses;
    private boolean bargain;
    private Sku sku;
    private Shop shop;
    private Category category;
    private List<Price> prices;
    private List<Offer> offers;

    @JsonCreator
    public Product(@JsonProperty("id") int skroutzId,
                   @JsonProperty("name") String name,
                   @JsonProperty("sku_id") int skuId,
                   @JsonProperty("shop_id") int shopId,
                   @JsonProperty("category_id") int categoryId,
                   @JsonProperty("availability") String availability,
                   @JsonProperty("click_url") String clickUrl,
                   @JsonProperty("shop_uid") String shopUid,
                   @JsonProperty("price") float price,
                   @JsonProperty("expenses") String expenses) {
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
        this.expenses = expenses;
    }

    public Product() {
    }

    @Override
    public <T extends SkroutzEntity> void updateFrom(T restEntity) {
        this.name = ((Product) restEntity).name;
        this.availability = ((Product) restEntity).availability;
        this.clickUrl = ((Product) restEntity).clickUrl;
        this.shopUid = ((Product) restEntity).shopUid.length() > 64 ? ((Product) restEntity).shopUid.substring(0, 64) : ((Product) restEntity).shopUid;
        this.price = ((Product) restEntity).price;
        this.skuId = ((Product) restEntity).skuId;
        this.categoryId = ((Product) restEntity).categoryId;
        this.shopId = ((Product) restEntity).shopId;
        if (restEntity.getEtag() != null) {
            this.etag = restEntity.getEtag();
        }
        if (this.prices == null) {
            this.prices = new ArrayList<>();
        }
        if (!this.prices.stream().anyMatch(p -> p.getPrice() == this.price)) {
            Price price = Price.fromProduct(this);
            this.prices.add(price);
        }
    }

    @Id
    @GeneratedValue(generator = "ProductSequence")
    @SequenceGenerator(name = "ProductSequence", sequenceName = "prod_seq", allocationSize = 1)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    @Size(min = 1, max = 300)
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @Column(name = "sku_id")
    public int getSkuId() {
        return skuId;
    }

    public void setSkuId(int skuId) {
        this.skuId = skuId;
    }

    @NotNull
    @Column(name = "shop_id")
    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    @NotNull
    @Column(name = "category_id")
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Size(max = 50)
    @Column(name = "availability")
    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @Size(max = 300)
    @Column(name = "click_url")
    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    @Size(max = 64)
    @Column(name = "shop_uid")
    public String getShopUid() {
        return shopUid;
    }

    public void setShopUid(String shopUid) {
        this.shopUid = shopUid;
    }

    @NotNull
    @Column(name = "price", precision = 2)
    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @NotNull
    @Column(name = "price_changes")
    public int getPriceChanges() {
        return priceChanges;
    }

    public void setPriceChanges(int priceChanges) {
        this.priceChanges = priceChanges;
    }

    @NotNull
    @Column(name = "average_past_price", precision = 2)
    public float getAveragePastPrice() {
        return averagePastPrice;
    }

    public void setAveragePastPrice(float averagePastPrice) {
        this.averagePastPrice = averagePastPrice;
    }

    @Column(name = "is_bargain")
    public boolean isBargain() {
        return bargain;
    }

    public void setBargain(boolean bargain) {
        this.bargain = bargain;
    }

    @Transient
    public String getExpenses() {
        return expenses;
    }

    public void setExpenses(String expenses) {
        this.expenses = expenses;
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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

    @ManyToOne
    @JoinColumn(name = "sku_id", referencedColumnName = "skroutz_id", insertable = false, updatable = false)
    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    @ManyToOne
    @JoinColumn(name = "shop_id", referencedColumnName = "skroutz_id", insertable = false, updatable = false)
    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "skroutz_id", insertable = false, updatable = false)
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
        return Objects.equals(skuId, product.skuId) &&
                Objects.equals(shopId, product.shopId) &&
                Objects.equals(categoryId, product.categoryId) &&
                Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, skuId, shopId, categoryId);
    }

    public static Product getCheapest(List<Product> products) {
        return products.stream().min((product, t1) -> {
            if (product.getPrice() > t1.getPrice()) {
                return 1;
            } else if (product.getPrice() == t1.getPrice()) {
                return 0;
            } else {
                return -1;
            }
        }).orElse(null);
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }
}