package gr.ntua.cn.zannis.bargains.webapp.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * A Base class to mark our custom persistent classes. Also includes common fields
 * among all custom classes.
 * @author zannis <zannis.kal@gmail.com
 */
@MappedSuperclass
public abstract class SkroutzEntity implements Serializable {

    private static final long serialVersionUID = -1L;

    protected int skroutzId;
    private Date insertedAt;
    protected Date checkedAt;
    private Date modifiedAt;
    protected String etag;

    public SkroutzEntity() {
    }

    @PrePersist
    private void prePersist() {
        Date now = new Date();
        this.checkedAt = now;
        this.insertedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        Date now = new Date();
        this.checkedAt = now;
        this.modifiedAt = now;
    }

    @NotNull
    @Column(name = "skroutz_id")
    public int getSkroutzId() {
        return skroutzId;
    }

    public void setSkroutzId(int skroutzId) {
        this.skroutzId = skroutzId;
    }

    @Size(max = 32)
    @Column(name = "etag")
    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "inserted_at")
    public Date getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Date insertedAt) {
        this.insertedAt = insertedAt;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "checked_at")
    public Date getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(Date checkedAt) {
        this.checkedAt = checkedAt;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at")
    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Transient
    public boolean hasEtag() {
        return this.etag != null && !this.etag.isEmpty();
    }

    public abstract <T extends SkroutzEntity> void updateFrom(T restEntity);
}
