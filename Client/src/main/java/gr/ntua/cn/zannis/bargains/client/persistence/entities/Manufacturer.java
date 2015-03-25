package gr.ntua.cn.zannis.bargains.client.persistence.entities;

import gr.ntua.cn.zannis.bargains.client.persistence.PersistentEntity;

import java.util.List;

/**
 * Persistent class for the Manufacturer entity.
 * @author zannis <zannis.kal@gmail.com
 */
public class Manufacturer extends PersistentEntity {

    protected static final long serialVersionUID = -1L;

    private long id;
    private String name;
    private String imageUrl;
    private List<Category> categories;

    // TODO add jsoncreator
    private List<Sku> skus;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }
}
