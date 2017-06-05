package gr.ntua.cn.zannis.bargains.webapp.persistence.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * The Category persistent entity.
 * @author zannis <zannis.kal@gmail.com>
 */
@Entity
@JsonRootName("category")
@JsonIgnoreProperties({"adult"})
@Table(name = "categories", schema = "public", catalog = "bargainhunt")
@NamedQueries({
        @NamedQuery(name = "Category.findAll", query = "select c from Category c"),
        @NamedQuery(name = "Category.findSome", query = "select c from Category c where c.id in (:ids)"),
        @NamedQuery(name = "Category.findCrawled", query = "select c from Category c where size(c.skus) > 0"),
        @NamedQuery(name = "Category.findBySkroutzId", query = "select c from Category c where c.skroutzId = :skroutzId")
})
public class Category extends SkroutzEntity {

    protected static final long serialVersionUID = -1L;

    private int id;
    private String name;
    private int childrenCount;
    private String imageUrl;
    private int parentId;
    private boolean fashion;
    private String layoutMode;
    private String webUri;
    private String code;
    private String path;
    private boolean showSpecifications;
    private String manufacturerTitle;
    private int matchCount;
    private Category parent;
    private List<Category> children;
    private List<Sku> skus;
    private int familyId;
    private boolean showManufacturersFilter;

    @JsonCreator
    public Category(@JsonProperty("id") int skroutzId,
                    @JsonProperty("name") String name,
                    @JsonProperty("children_count") int childrenCount,
                    @JsonProperty("image_url") String imageUrl,
                    @JsonProperty("parent_id") int parentId,
                    @JsonProperty("fashion") boolean fashion,
                    @JsonProperty("layout_mode") String layoutMode,
                    @JsonProperty("web_uri") String webUri,
                    @JsonProperty("code") String code,
                    @JsonProperty("path") String path,
                    @JsonProperty("show_specifications") boolean showSpecifications,
                    @JsonProperty("manufacturer_title") String manufacturerTitle,
                    @JsonProperty("match_count") int matchCount,
                    @JsonProperty("family_id") int familyId,
                    @JsonProperty("show_manufacturers_filter") boolean showManufacturersFilter
    ) {
        super();
        this.skroutzId = skroutzId;
        this.name = name;
        this.childrenCount = childrenCount;
        this.webUri = webUri;
        this.imageUrl = imageUrl;
        this.parentId = parentId;
        this.fashion = fashion;
        this.path = path;
        this.code = code;
        this.showSpecifications = showSpecifications;
        this.manufacturerTitle = manufacturerTitle;
        this.matchCount = matchCount;
        this.layoutMode = layoutMode;
        this.familyId = familyId;
        this.showManufacturersFilter = showManufacturersFilter;
    }

    public Category() {
    }

    @Override
    public <T extends SkroutzEntity> void updateFrom(T restEntity) {
        this.name = ((Category) restEntity).getName();
        this.imageUrl = ((Category) restEntity).getImageUrl();
        this.parentId = ((Category) restEntity).getParentId();
        this.etag = restEntity.getEtag();
    }

    @Id
    @GeneratedValue(generator = "CategorySequence")
    @SequenceGenerator(name = "CategorySequence", sequenceName = "category_seq", allocationSize = 1)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Column(name = "parent_id")
    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
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
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    @Transient
    public String getLayoutMode() {
        return layoutMode;
    }

    public void setLayoutMode(String layoutMode) {
        this.layoutMode = layoutMode;
    }

    @Transient
    public String getWebUri() {
        return webUri;
    }

    public void setWebUri(String webUri) {
        this.webUri = webUri;
    }

    @Transient
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "skroutz_id", insertable = false, updatable = false)
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
    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    @Transient
    public int getFamilyId() {
        return familyId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

    @Transient
    public boolean isShowManufacturersFilter() {
        return showManufacturersFilter;
    }

    public void setShowManufacturersFilter(boolean showManufacturersFilter) {
        this.showManufacturersFilter = showManufacturersFilter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return new EqualsBuilder()
                .append(id, category.id)
                .append(childrenCount, category.childrenCount)
                .append(parentId, category.parentId)
                .append(fashion, category.fashion)
                .append(showSpecifications, category.showSpecifications)
                .append(matchCount, category.matchCount)
                .append(name, category.name)
                .append(imageUrl, category.imageUrl)
                .append(layoutMode, category.layoutMode)
                .append(webUri, category.webUri)
                .append(code, category.code)
                .append(path, category.path)
                .append(manufacturerTitle, category.manufacturerTitle)
                .append(showManufacturersFilter, category.showManufacturersFilter)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(skroutzId)
                .append(parentId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .toString();
    }
}
