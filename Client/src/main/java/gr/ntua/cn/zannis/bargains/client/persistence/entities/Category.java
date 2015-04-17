package gr.ntua.cn.zannis.bargains.client.persistence.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import gr.ntua.cn.zannis.bargains.client.persistence.SkroutzEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

/**
 * The Category persistent entity.
 * @author zannis <zannis.kal@gmail.com>
 */
@Entity
@JsonRootName("category")
@Table(name = "categories", schema = "public", catalog = "bargainhunt")
@NamedQueries({
        @NamedQuery(name = "Category.findAll", query = "select c from Category c"),
        @NamedQuery(name = "Category.findBySkroutzId", query = "select c from Category c where c.skroutzId = :skroutzId")
})
public class Category extends SkroutzEntity {

    protected static final long serialVersionUID = -1L;

    private long id;
    private String name;
    private int childrenCount;
    private String imageUrl;
    private long parentId;
    private boolean fashion;
    private String parentPath;
    private boolean showSpecifications;
    private String manufacturerTitle;
    private int matchCount;
    private Category parent;
    private List<Category> children;
    private List<Sku> skus;

    @JsonCreator
    public Category(@JsonProperty("id") long skroutzId,
                    @JsonProperty("name") String name,
                    @JsonProperty("children_count") int childrenCount,
                    @JsonProperty("image_url") String imageUrl,
                    @JsonProperty("parent_id") long parentId,
                    @JsonProperty("fashion") boolean fashion,
                    @JsonProperty("path") String parentPath,
                    @JsonProperty("show_specifications") boolean showSpecifications,
                    @JsonProperty("manufacturer_title") String manufacturerTitle,
                    @JsonProperty("match_count") int matchCount) {
        super();
        this.skroutzId = skroutzId;
        this.name = name;
        this.childrenCount = childrenCount;
        this.imageUrl = imageUrl;
        this.parentId = parentId;
        this.fashion = fashion;
        this.parentPath = parentPath;
        this.showSpecifications = showSpecifications;
        this.manufacturerTitle = manufacturerTitle;
        this.matchCount = matchCount;
    }

    public Category() {
    }

    @Id
    @GeneratedValue(generator = "CategorySequence")
    @SequenceGenerator(name = "CategorySequence", sequenceName = "category_seq", allocationSize = 1)
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Size(min = 1, max = 100)
    @NotNull
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }

    @Size(max = 300)
    @Column(name = "image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    @Transient
    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Transient
    public boolean isFashion() {
        return fashion;
    }

    public void setFashion(boolean fashion) {
        this.fashion = fashion;
    }

    @Transient
    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    @Transient
    public boolean isShowSpecifications() {
        return showSpecifications;
    }

    public void setShowSpecifications(boolean showSpecifications) {
        this.showSpecifications = showSpecifications;
    }

    @Transient
    public String getManufacturerTitle() {
        return manufacturerTitle;
    }

    public void setManufacturerTitle(String manufacturerTitle) {
        this.manufacturerTitle = manufacturerTitle;
    }

    @Transient
    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "skroutz_id")
    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy = "parent")
    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    @OneToMany(mappedBy = "category")
    public List<Sku> getSku() {
        return skus;
    }

    public void setSku(List<Sku> skus) {
        this.skus = skus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) &&
                Objects.equals(childrenCount, category.childrenCount) &&
                Objects.equals(parentId, category.parentId) &&
                Objects.equals(fashion, category.fashion) &&
                Objects.equals(showSpecifications, category.showSpecifications) &&
                Objects.equals(name, category.name) &&
                Objects.equals(imageUrl, category.imageUrl) &&
                Objects.equals(parentPath, category.parentPath) &&
                Objects.equals(manufacturerTitle, category.manufacturerTitle) &&
                Objects.equals(parent, category.parent) &&
                Objects.equals(children, category.children) &&
                Objects.equals(skus, category.skus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, imageUrl, parentId, etag);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("skroutzId", skroutzId)
                .append("name", name)
                .append("parentId", parentId)
                .toString();
    }
}
