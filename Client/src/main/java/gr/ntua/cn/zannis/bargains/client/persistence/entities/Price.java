package gr.ntua.cn.zannis.bargains.client.persistence.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
@Entity
@Table(name = "prices", schema = "public", catalog = "bargainhunt")
@NamedQueries({
        @NamedQuery(name = "Price.findAll", query = "select p from Price p"),
        @NamedQuery(name = "Price.findAllByProduct", query = "select p from Price p where p.product = :product")
})
public class Price {
    private long id;
    private float price;
    private Date checkedAt;
    private Product product;

    @Id
    @GeneratedValue(generator = "PriceSequence")
    @SequenceGenerator(name = "PriceSequence", sequenceName = "price_seq", allocationSize = 1)
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    @Column(name = "checked_at")
    public Date getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(Date checkedAt) {
        this.checkedAt = checkedAt;
    }

    @ManyToOne
    @NotNull
    @JoinColumn(name = "product_id", referencedColumnName = "skroutz_id")
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price1 = (Price) o;
        return Objects.equals(id, price1.id) &&
                Objects.equals(price, price1.price) &&
                Objects.equals(checkedAt, price1.checkedAt) &&
                Objects.equals(product, price1.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, checkedAt, product);
    }
}
