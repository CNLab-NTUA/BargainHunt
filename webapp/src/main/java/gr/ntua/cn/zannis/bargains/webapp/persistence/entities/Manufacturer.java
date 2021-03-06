package gr.ntua.cn.zannis.bargains.webapp.persistence.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Persistent class for the Manufacturer entity.
 * @author zannis <zannis.kal@gmail.com
 */
@JsonRootName("manufacturer")
@Entity
@Table(name = "manufacturers", schema = "public", catalog = "bargainhunt")
@NamedQueries({
        @NamedQuery(name = "Manufacturer.findBySkroutzId", query = "select m from Manufacturer m where m.skroutzId = :skroutzId"),
        @NamedQuery(name = "Manufacturer.findAll", query = "select m from Manufacturer m")
})
public class Manufacturer extends SkroutzEntity {

    protected static final long serialVersionUID = -1L;

    private int id;
    private String name;
    private String imageUrl;

    public Manufacturer(@JsonProperty("id") int id,
                        @JsonProperty("name") String name,
                        @JsonProperty("image_url") String imageUrl) {
        super();
        this.skroutzId = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public Manufacturer() {
    }

    @Override
    public <T extends SkroutzEntity> void updateFrom(T restEntity) {
        this.name = ((Manufacturer) restEntity).name;
        this.imageUrl = ((Manufacturer) restEntity).imageUrl;
        if (restEntity.getEtag() != null) {
            this.etag = restEntity.getEtag();
        }
    }

    @Id
    @GeneratedValue(generator = "ManufacturerSequence")
    @SequenceGenerator(name = "ManufacturerSequence", sequenceName = "manufacturer_seq", allocationSize = 1)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    @Size(max = 100)
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Size(max = 300)
    @Column(name = "image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manufacturer that = (Manufacturer) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, imageUrl);
    }

    @Override
    public String toString() {
        return "Manufacturer{" + "id=" + id + ", name='" + name + '\'' + ", imageUrl='" + imageUrl + '\'' + '}';
    }
}
