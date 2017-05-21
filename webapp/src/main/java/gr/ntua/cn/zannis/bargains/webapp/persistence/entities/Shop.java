package gr.ntua.cn.zannis.bargains.webapp.persistence.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * The shop persistent entity containing JSON fields as well as custom database fields.
 * @author zannis <zannis.kal@gmail.com>
 */
@JsonRootName("shop")
@JsonIgnoreProperties({"latest_reviews_count", "web_uri", "extra_info", "top_positive_reasons"})
@Entity
@Table(name = "shops", schema = "public", catalog = "bargainhunt")
@NamedQueries({
        @NamedQuery(name = "Shop.findBySkroutzId", query = "select s from Shop s where s.skroutzId = :skroutzId"),
        @NamedQuery(name = "Shop.findAll", query = "select s from Shop s")
})
public class Shop extends SkroutzEntity {

    protected static final long serialVersionUID = -1L;

    private int id;
    private String name;
    private String link;
    private String phone;
    private String imageUrl;
    private String thumbshotUrl;
    private int reviewCount;
    private int reviewScore;
    private PaymentMethods paymentMethods;
    private Shipping shipping;
    private List<Product> products;

    @JsonCreator
    public Shop(@JsonProperty("id") int skroutzId,
                @JsonProperty("name") String name,
                @JsonProperty("link") String link,
                @JsonProperty("phone") String phone,
                @JsonProperty("image_url") String imageUrl,
                @JsonProperty("thumbshot_url") String thumbshotUrl,
                @JsonProperty("reviews_count") int reviewCount,
                @JsonProperty("review_score") int reviewScore,
                @JsonProperty("payment_methods") PaymentMethods paymentMethods,
                @JsonProperty("shipping") Shipping shipping) {

        super();
        this.skroutzId = skroutzId;
        this.name = name;
        this.link = link;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.thumbshotUrl = thumbshotUrl;
        this.reviewCount = reviewCount;
        this.reviewScore = reviewScore;
        this.paymentMethods = paymentMethods;
        this.shipping = shipping;
    }

    public Shop() {
    }

    @Override
    public <T extends SkroutzEntity> void updateFrom(T restEntity) {
        this.name = ((Shop) restEntity).getName();
        this.link = ((Shop) restEntity).getLink();
        this.phone = ((Shop) restEntity).getPhone();
        this.imageUrl = ((Shop) restEntity).getImageUrl();
        this.thumbshotUrl = ((Shop) restEntity).getThumbshotUrl();
        if (restEntity.getEtag() != null) {
            this.etag = restEntity.getEtag();
        }
    }

    @Id
    @GeneratedValue(generator = "ShopSequence")
    @SequenceGenerator(name = "ShopSequence", sequenceName = "shop_seq", allocationSize = 1)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @Size(max = 300)
    @Column(name = "link")
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Column(name = "phone", precision = 0)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Size(max = 300)
    @Column(name = "image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Size(max = 300)
    @Column(name = "thumbshot_url")
    public String getThumbshotUrl() {
        return thumbshotUrl;
    }

    public void setThumbshotUrl(String thumbshotUrl) {
        this.thumbshotUrl = thumbshotUrl;
    }

    @Transient
    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    @Transient
    public int getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(int reviewScore) {
        this.reviewScore = reviewScore;
    }

    @Transient
    public PaymentMethods getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(PaymentMethods paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shop shop = (Shop) o;
        return Objects.equals(id, shop.id) &&
                Objects.equals(reviewCount, shop.reviewCount) &&
                Objects.equals(reviewScore, shop.reviewScore) &&
                Objects.equals(name, shop.name) &&
                Objects.equals(link, shop.link) &&
                Objects.equals(phone, shop.phone) &&
                Objects.equals(imageUrl, shop.imageUrl) &&
                Objects.equals(thumbshotUrl, shop.thumbshotUrl) &&
                Objects.equals(paymentMethods, shop.paymentMethods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, link, phone, imageUrl, thumbshotUrl, reviewCount, reviewScore, paymentMethods);
    }

    @Transient
    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public static class PaymentMethods implements Serializable {
        private boolean creditCard;
        private boolean paypal;
        private boolean bank;
        private boolean cash;
        private String comments;

        @JsonCreator
        public PaymentMethods(@JsonProperty("credit_card") boolean creditCard,
                              @JsonProperty("paypal") boolean paypal,
                              @JsonProperty("bank") boolean bank,
                              @JsonProperty("spot_cash") boolean cash,
                              @JsonProperty("installments") String comments) {
            this.creditCard = creditCard;
            this.paypal = paypal;
            this.bank = bank;
            this.cash = cash;
            this.comments = comments;
        }

        public boolean isCreditCard() {
            return creditCard;
        }

        public void setCreditCard(boolean creditCard) {
            this.creditCard = creditCard;
        }

        public boolean isPaypal() {
            return paypal;
        }

        public void setPaypal(boolean paypal) {
            this.paypal = paypal;
        }

        public boolean isBank() {
            return bank;
        }

        public void setBank(boolean bank) {
            this.bank = bank;
        }

        public boolean isCash() {
            return cash;
        }

        public void setCash(boolean cash) {
            this.cash = cash;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }

    private static class Shipping implements Serializable {
        private boolean free;
        private int freeFrom;
        private String freeFromInfo;
        private boolean shippingCostEnabled;
        private float minPrice;

        @JsonCreator
        public Shipping(@JsonProperty("free") boolean free,
                        @JsonProperty("free_from") int freeFrom,
                        @JsonProperty("free_from_info") String freeFromInfo,
                        @JsonProperty("shipping_cost_enabled") boolean shippingCostEnabled,
                        @JsonProperty("min_price") float minPrice) {
            this.free = free;
            this.freeFrom = freeFrom;
            this.freeFromInfo = freeFromInfo;
            this.shippingCostEnabled = shippingCostEnabled;
            this.minPrice = minPrice;
        }

        public boolean isFree() {
            return free;
        }

        public void setFree(boolean free) {
            this.free = free;
        }

        public int getFreeFrom() {
            return freeFrom;
        }

        public void setFreeFrom(int freeFrom) {
            this.freeFrom = freeFrom;
        }

        public String getFreeFromInfo() {
            return freeFromInfo;
        }

        public void setFreeFromInfo(String freeFromInfo) {
            this.freeFromInfo = freeFromInfo;
        }

        public float getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(float minPrice) {
            this.minPrice = minPrice;
        }

        public boolean isShippingCostEnabled() {
            return shippingCostEnabled;
        }

        public void setShippingCostEnabled(boolean shippingCostEnabled) {
            this.shippingCostEnabled = shippingCostEnabled;
        }
    }

    @Override
    public String toString() {
        return "Shop{" +
                "skroutzId='" + skroutzId + '\'' +
                "name='" + name + '\'' +
                '}';
    }
}
