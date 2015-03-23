package gr.ntua.cn.zannis.bargains.client.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Date;
import java.util.List;

/**
 * The Category persistent entity.
 * @author zannis <zannis.kal@gmail.com>
 */
@JsonRootName("category")
public class Category {
    private long id;
    private long skroutzId;
    private String name;
    private int childrenCount;
    private String imageUrl;
    private long parentId;
    private boolean fashion;
    private List<Long> parentPath;
    private boolean showSpecifications;
    private String manufacturerTitle;
    private Date insertedAt;

    @JsonCreator
    public Category(@JsonProperty("id") long skroutzId,
                    @JsonProperty("name") String name,
                    @JsonProperty("children_count") int childrenCount,
                    @JsonProperty("image_url") String imageUrl,
                    @JsonProperty("parent_id") long parentId,
                    @JsonProperty("fashion") boolean fashion,
                    @JsonProperty("parent_path") List<Long> parentPath,
                    @JsonProperty("show_specifications") boolean showSpecifications,
                    @JsonProperty("manufacturer_title") String manufacturerTitle) {
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

    public List<Long> getParentPath() {
        return parentPath;
    }

    public void setParentPath(List<Long> parentPath) {
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

    public Date getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Date insertedAt) {
        this.insertedAt = insertedAt;
    }
}
