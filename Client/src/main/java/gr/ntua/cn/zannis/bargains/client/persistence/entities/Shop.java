package gr.ntua.cn.zannis.bargains.client.persistence.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import gr.ntua.cn.zannis.bargains.client.persistence.SkroutzEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

/**
 * The shop persistent entity containing JSON fields as well as custom database fields.
 * @author zannis <zannis.kal@gmail.com>
 */
@JsonRootName("shop")
@Entity
@Table(name = "shops", schema = "public")
@NamedQuery(name = "Shop.findAll", query = "select s from Shop s")
public class Shop extends SkroutzEntity {

    protected static final long serialVersionUID = -1L;

    private long id;
    private String name;
    private String link;
    private String phone;
    private String imageUrl;
    private String thumbshotUrl;
    private int reviewCount;
    private int reviewScore;
    private PaymentMethods paymentMethods;
    private List<Product> products;

    @JsonCreator
    public Shop(@JsonProperty("id") long skroutzId,
                @JsonProperty("name") String name,
                @JsonProperty("link") String link,
                @JsonProperty("phone") String phone,
                @JsonProperty("image_url") String imageUrl,
                @JsonProperty("thumbshot_url") String thumbshotUrl,
                @JsonProperty("reviews_count") int reviewCount,
                @JsonProperty("review_score") int reviewScore,
                @JsonProperty("payment_methods") PaymentMethods paymentMethods) {
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
    }

    public Shop() {
    }

    @Id
    @GeneratedValue(generator = "ShopSequence")
    @SequenceGenerator(name = "ShopSequence", sequenceName = "shop_seq", allocationSize = 1)
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    @OneToMany(mappedBy = "shop")
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

    public static class PaymentMethods {
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
}
