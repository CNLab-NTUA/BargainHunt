package gr.ntua.cn.zannis.bargains.client.persistence.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import gr.ntua.cn.zannis.bargains.client.persistence.PersistentEntity;

/**
 * The Category persistent entity.
 * @author zannis <zannis.kal@gmail.com>
 */
@JsonRootName("category")
public class Category extends PersistentEntity {

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

    @JsonCreator
    public Category(@JsonProperty("id") long skroutzId,
                    @JsonProperty("name") String name,
                    @JsonProperty("children_count") int childrenCount,
                    @JsonProperty("image_url") String imageUrl,
                    @JsonProperty("parent_id") long parentId,
                    @JsonProperty("fashion") boolean fashion,
                    @JsonProperty("path") String parentPath,
                    @JsonProperty("show_specifications") boolean showSpecifications,
                    @JsonProperty("manufacturer_title") String manufacturerTitle) {
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
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public boolean isFashion() {
        return fashion;
    }

    public void setFashion(boolean fashion) {
        this.fashion = fashion;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public boolean isShowSpecifications() {
        return showSpecifications;
    }

    public void setShowSpecifications(boolean showSpecifications) {
        this.showSpecifications = showSpecifications;
    }

    public String getManufacturerTitle() {
        return manufacturerTitle;
    }

    public void setManufacturerTitle(String manufacturerTitle) {
        this.manufacturerTitle = manufacturerTitle;
    }
}
