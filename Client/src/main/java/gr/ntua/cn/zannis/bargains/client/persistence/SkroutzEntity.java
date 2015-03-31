package gr.ntua.cn.zannis.bargains.client.persistence;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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

    @Column(name = "skroutz_id", nullable = false, insertable = false, updatable = false)
    public long getSkroutzId() {
        return skroutzId;
    }

    public void setSkroutzId(long skroutzId) {
        this.skroutzId = skroutzId;
    }

    @Column(name = "etag", nullable = true, insertable = false, updatable = false, length = 32)
    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "inserted_at", nullable = false, insertable = false, updatable = false)
    public Date getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Date insertedAt) {
        this.insertedAt = insertedAt;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "checked_at", nullable = true, insertable = false, updatable = false)
    public Date getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(Date checkedAt) {
        this.checkedAt = checkedAt;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at", nullable = true, insertable = false, updatable = false)
    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public void wasJustChecked() {
        this.checkedAt = new Date();
    }
}
