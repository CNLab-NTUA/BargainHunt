package gr.ntua.cn.zannis.bargains.webapp.persistence.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import static gr.ntua.cn.zannis.bargains.statistics.Tester.Flexibility;

/**
 * @author zannis <zannis.kal@gmail.com
 */
@Entity
@Table(name = "offers", schema = "public", catalog = "bargainhunt")
@NamedQueries({
        @NamedQuery(name = "Offer.findAll", query = "select o from Offer o"),
        @NamedQuery(name = "Offer.findActive", query = "select o from Offer o where " +
                "o.insertedAt > :someDate and " +
                "o.finishedAt is null")
})
public class Offer implements Serializable {

    public static final long serialVersionUID = -1L;

    private int id;
    private int productId;
    private int priceId;
    private short acceptedBy;
    private Flexibility grubbsFlexibility;
    private Flexibility chauvenetFlexibility;
    private Flexibility quartileFlexibility;
    private Date insertedAt;
    private Date finishedAt;
    private Date checkedAt;
    private Price price;
    private Product product;

    public Offer() {
    }

    @Override
    public String toString() {
        return "Offer{" + id +
                ", product=" + product.getName() +
                ", price=" + price.getPrice() +
                ", acceptedBy=" + acceptedBy +
                ", grubbsFlexibility=" + grubbsFlexibility +
                ", chauvenetFlexibility=" + chauvenetFlexibility +
                ", quartileFlexibility=" + quartileFlexibility +
                '}';
    }

    public Offer(Product product, Price price, short acceptedBy, Flexibility grubbs,
                 Flexibility chauvenet, Flexibility quartile) {
        this.product = product;
        if (this.product.getOffers() == null) {
            this.product.setOffers(new ArrayList<>());
        }
        this.product.getOffers().add(this);
        this.price = price;
        this.acceptedBy = acceptedBy;
        this.grubbsFlexibility = grubbs;
        this.chauvenetFlexibility = chauvenet;
        this.quartileFlexibility = quartile;
    }

    @PrePersist
    private void prePersist() {
        Date now = new Date();
        this.checkedAt = now;
        this.insertedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        this.checkedAt = new Date();
    }


    @Id
    @GeneratedValue(generator = "OfferSequence")
    @SequenceGenerator(name = "OfferSequence", sequenceName = "offer_seq", allocationSize = 1)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    @Column(name = "product_id")
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @NotNull
    @Column(name = "price_id")
    public int getPriceId() {
        return priceId;
    }

    public void setPriceId(int priceId) {
        this.priceId = priceId;
    }

    @NotNull
    @Column(name = "accepted_by")
    public short getAcceptedBy() {
        return acceptedBy;
    }

    public void setAcceptedBy(short acceptedBy) {
        this.acceptedBy = acceptedBy;
    }

    @NotNull
    @Column(name = "flexibility")
    public Flexibility getGrubbsFlexibility() {
        return grubbsFlexibility;
    }

    public void setGrubbsFlexibility(Flexibility grubbsFlexibility) {
        this.grubbsFlexibility = grubbsFlexibility;
    }

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "inserted_at")
    public Date getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Date insertedAt) {
        this.insertedAt = insertedAt;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "finished_at")
    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "checked_at")
    public Date getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(Date checkedAt) {
        this.checkedAt = checkedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Offer offers = (Offer) o;

        return new EqualsBuilder()
                .append(id, offers.id)
                .append(productId, offers.productId)
                .append(priceId, offers.priceId)
                .append(acceptedBy, offers.acceptedBy)
                .append(grubbsFlexibility, offers.grubbsFlexibility)
                .append(insertedAt, offers.insertedAt)
                .append(finishedAt, offers.finishedAt)
                .append(checkedAt, offers.checkedAt)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(productId)
                .append(priceId)
                .append(acceptedBy)
                .append(grubbsFlexibility)
                .append(insertedAt)
                .append(finishedAt)
                .append(checkedAt)
                .toHashCode();
    }

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "price_id", referencedColumnName = "id")
    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "product_id", referencedColumnName = "id")
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public static short calculateAcceptedBy(boolean grubbsResult, boolean chauvenetResult, boolean quartileResult) {
        if (grubbsResult) {
            if (chauvenetResult) {
                if (quartileResult) {
                    return 7;
                } else {
                    return 4;
                }
            } else {
                if (quartileResult) {
                    return 5;
                } else {
                    return 1;
                }
            }
        } else {
            if (chauvenetResult) {
                if (quartileResult) {
                    return 6;
                } else {
                    return 2;
                }
            } else {
                if (quartileResult) {
                    return 3;
                } else {
                    return 0;
                }
            }
        }
    }

    public Flexibility getChauvenetFlexibility() {
        return chauvenetFlexibility;
    }

    public void setChauvenetFlexibility(Flexibility chauvenetFlexibility) {
        this.chauvenetFlexibility = chauvenetFlexibility;
    }

    public Flexibility getQuartileFlexibility() {
        return quartileFlexibility;
    }

    public void setQuartileFlexibility(Flexibility quartileFlexibility) {
        this.quartileFlexibility = quartileFlexibility;
    }
}
