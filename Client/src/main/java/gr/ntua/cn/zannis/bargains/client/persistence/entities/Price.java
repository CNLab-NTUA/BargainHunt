package gr.ntua.cn.zannis.bargains.client.persistence.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
@Entity
@Table(name = "prices", schema = "public")
@NamedQueries({
        @NamedQuery(name = "findAll", query = "select p from Price p"),
        @NamedQuery(name = "findAllByProduct", query = "select p from Price p where p.product = :product")
})
public class Price {
    private Integer id;
    private BigDecimal price;
    private Date checkedAt;
    private Product product;

    @Id
    @GeneratedValue(generator = "PriceSequence")
    @SequenceGenerator(name = "PriceSequence", sequenceName = "price_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "price", nullable = false, insertable = false, updatable = false, precision = 2)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name = "checked_at", nullable = false, insertable = false, updatable = false)
    public Date getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(Date checkedAt) {
        this.checkedAt = checkedAt;
    }

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
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
