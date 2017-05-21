package gr.ntua.cn.zannis.bargains.webapp.ejb.dao.impl;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import gr.ntua.cn.zannis.bargains.webapp.ejb.dao.RequestDAO;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Request;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */

@Stateless
@LocalBean
public class RequestDAOImpl implements RequestDAO {

    public static final Logger log = LoggerFactory.getLogger(RequestDAO.class);

    @PersistenceContext
    EntityManager em;

    @Override
    public Request findByUrl(String url) {
        TypedQuery<Request> q = em.createNamedQuery("Request.findByUrl", Request.class);
        return q.setParameter("url", url).getSingleResult();
    }

    @Override
    public Request findByEtag(String etag) {
        TypedQuery<Request> q = em.createNamedQuery("Request.findByEtag", Request.class);
        return q.setParameter("etag", etag).getSingleResult();
    }

    @Override
    public List<Request> findAll() {
        TypedQuery<Request> q = em.createNamedQuery("Request.findAll", Request.class);
        return q.getResultList();
    }

    @Override
    public Request saveOrUpdate(Request request) {
        try {
            Request result = findByUrl(request.getUrl());
            result.updateFrom(request);
            log.info("Request " + request.getUrl() + " was successfully merged.");
            return result;
        } catch (NoResultException e) {
            // query doesnt exist in the db, persist it
            try {
                em.persist(request);
                log.info("Request " + request.getUrl() + " was successfully persisted.");
                return request;
            } catch (Exception e1) {
                log.error("Error trying to persist request " + request, e1);
                return null;
            }
        }
    }
}
