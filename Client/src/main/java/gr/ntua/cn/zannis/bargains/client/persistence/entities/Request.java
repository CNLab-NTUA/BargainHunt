package gr.ntua.cn.zannis.bargains.client.persistence.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * The Request entity, which actually logs all the requests made to the api and saves their etag to
 * avoid redundant calls.
 * @author zannis <zannis.kal@gmail.com>
 */
@Entity
@Table(name = "requests", schema = "public", catalog = "bargainhunt")
public class Request {
    private int id;
    private String requestUri;
    private String etag;
    private Date checkedAt;

    public Request(String requestUri, String etag, boolean successful) {
        this.requestUri = requestUri;
        this.etag = etag;
    }

    public Request() {
    }

    @PrePersist
    private void init() {
        this.checkedAt = new Date();
    }

    @Id
    @GeneratedValue(generator = "RequestSequence")
    @SequenceGenerator(name = "RequestSequence", sequenceName = "request_seq", allocationSize = 1)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "url")
    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    @Size(max = 32)
    @Column(name = "etag")
    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "checked_at")
    public Date getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(Date checkedAt) {
        this.checkedAt = checkedAt;
    }
}
