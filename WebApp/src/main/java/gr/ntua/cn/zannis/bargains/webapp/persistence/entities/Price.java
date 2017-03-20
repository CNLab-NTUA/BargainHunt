package gr.ntua.cn.zannis.bargains.webapp.persistence.entities;

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
        @NamedQuery(name = "Price.findOrderedByProduct", query = "select p from Price p where p.product = :product order by p.checkedAt desc"),
        @NamedQuery(name = "Price.findByProduct", query = "select p from Price p where p.product = :product"),
        @NamedQuery(name = "Price.findBySku", query = "select p from Price p where p.product.sku = :sku")
})
public class Price {
    private int id;
    private float price;
    private Date checkedAt;
    private Product product;
    private int productId;

    @Id
    @GeneratedValue(generator = "PriceSequence")
    @SequenceGenerator(name = "PriceSequence", sequenceName = "price_seq", allocationSize = 1)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable = false, updatable = false)
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @NotNull
    @Column(name = "product_id")
    public int getProductId() {
        return productId;
    }

    public void setProductId(int product_id) {
        this.productId = product_id;
    }

    @PrePersist
    private void prePersist() {
        this.checkedAt = new Date();
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

    public static Price fromProduct(Product product) {
        Price result = new Price();
        result.setPrice(product.getPrice());
        result.setProduct(product);
        result.setProductId(product.getId());
        return result;
    }

}
