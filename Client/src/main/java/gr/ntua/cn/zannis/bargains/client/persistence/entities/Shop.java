package gr.ntua.cn.zannis.bargains.client.persistence.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import gr.ntua.cn.zannis.bargains.client.persistence.PersistentEntity;

/**
 * The shop persistent entity containing JSON fields as well as custom database fields.
 * @author zannis <zannis.kal@gmail.com>
 */
@JsonRootName("shop")
public class Shop extends PersistentEntity {
    private long id;
    private long skroutzId;
    private String name;
    private String link;
    private String phone;
    private String imageUrl;
    private String thumbshotUrl;
    private int reviewCount;
    private int reviewScore;
    private PaymentMethods paymentMethods;
    private int bargainsFound;

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

    public static class Review {}

    public static class Location {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSkroutzId() {
        return skroutzId;
    }

    public void setSkroutzId(long skroutzId) {
        this.skroutzId = skroutzId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbshotUrl() {
        return thumbshotUrl;
    }

    public void setThumbshotUrl(String thumbshotUrl) {
        this.thumbshotUrl = thumbshotUrl;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(int reviewScore) {
        this.reviewScore = reviewScore;
    }

    public PaymentMethods getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(PaymentMethods paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public int getBargainsFound() {
        return bargainsFound;
    }

    public void setBargainsFound(int bargainsFound) {
        this.bargainsFound = bargainsFound;
    }
}
