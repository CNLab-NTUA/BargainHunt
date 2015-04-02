package gr.ntua.cn.zannis.bargains.client.persistence.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import gr.ntua.cn.zannis.bargains.client.persistence.SkroutzEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Persistent class for the Manufacturer entity.
 * @author zannis <zannis.kal@gmail.com
 */
@JsonRootName("manufacturer")
@Entity
@Table(name = "manufacturers", schema = "public")
@NamedQuery(name = "Manufacturer.findAll", query = "select m from Manufacturer m")
public class Manufacturer extends SkroutzEntity {

    protected static final long serialVersionUID = -1L;

    private long id;
    private String name;
    private String imageUrl;

    public Manufacturer(@JsonProperty("id") long id,
                        @JsonProperty("name") String name,
                        @JsonProperty("image_url") String imageUrl) {
        super();
        this.skroutzId = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public Manufacturer() {
    }

    @Id
    @GeneratedValue(generator = "ManufacturerSequence")
    @SequenceGenerator(name = "ManufacturerSequence", sequenceName = "manufacturer_seq", allocationSize = 1)
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
