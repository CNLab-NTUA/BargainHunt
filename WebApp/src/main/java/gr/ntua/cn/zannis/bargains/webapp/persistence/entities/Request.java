package gr.ntua.cn.zannis.bargains.webapp.persistence.entities;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * The Request entity, which actually logs all the requests made to the api and saves their etag to
 * avoid redundant calls.
 *
 * @author zannis <zannis.kal@gmail.com>
 */
@Entity
@Table(name = "requests", schema = "public", catalog = "bargainhunt")
@NamedQuery(name = "Request.findByUri", query = "select r from Request r where r.requestUri = :uri")
public class Request {
    private int id;
    private String requestUri;
    private String etag;
    private int count;
    private Date checkedAt;

    public Request(String requestUri, String etag) {
        this.requestUri = requestUri;
        this.etag = etag;
    }

    public Request() {
    }

    @PrePersist
    private void prePersist() {
        this.checkedAt = new Date();
        this.count = 1;
    }

    @PreUpdate
    private void preUpdate() {
        this.checkedAt = new Date();
        this.count++;
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
    @Column(name = "request_uri")
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

    @NotNull
    @Column(name = "count")
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("requestUri", requestUri)
                .append("etag", etag)
                .append("checkedAt", checkedAt)
                .toString();
    }
}
