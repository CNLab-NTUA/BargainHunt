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
public class SkroutzEntity implements Serializable {

    private static final long serialVersionUID = -1L;

    protected long skroutzId;
    protected Date insertedAt;
    protected Date checkedAt;
    protected Date modifiedAt;
    protected String etag;

    public SkroutzEntity() {
        Date now = new Date();
        this.checkedAt = now;
        this.insertedAt = now;
    }

    @NotNull
    @Column(name = "skroutz_id")
    public long getSkroutzId() {
        return skroutzId;
    }

    public void setSkroutzId(long skroutzId) {
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
    public void wasJustChecked() {
        this.checkedAt = new Date();
    }

    @Transient
    public boolean hasEtag() {
        return this.etag != null;
    }
}
