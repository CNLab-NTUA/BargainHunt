package gr.ntua.cn.zannis.bargains.client.persistence;

import java.io.Serializable;
import java.util.Date;

/**
 * A Base class to mark our custom persistent classes. Also includes common fields
 * among all custom classes.
 * @author zannis <zannis.kal@gmail.com
 */
public class SkroutzEntity implements Serializable {

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
    public long getSkroutzId() {
        return skroutzId;
    }

    public void setSkroutzId(long skroutzId) {
        this.skroutzId = skroutzId;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Date getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Date insertedAt) {
        this.insertedAt = insertedAt;
    }

    public Date getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(Date checkedAt) {
        this.checkedAt = checkedAt;
    }

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
