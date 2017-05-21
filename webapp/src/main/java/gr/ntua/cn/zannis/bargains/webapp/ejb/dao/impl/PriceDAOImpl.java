package gr.ntua.cn.zannis.bargains.webapp.ejb.dao.impl;

import gr.ntua.cn.zannis.bargains.webapp.ejb.dao.PriceDAO;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Price;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author zannis zannis.kal@gmail.com
 */

@Stateless
@LocalBean
public class PriceDAOImpl implements PriceDAO {

    public static final Logger log = LoggerFactory.getLogger(PriceDAO.class);

    @PersistenceContext
    EntityManager em;

    public List<Price> findByProduct(Integer productSkroutzId) {
        TypedQuery<Price> q = em.createNamedQuery("Price.findByProduct", Price.class);
        return q.setParameter("skroutzId", productSkroutzId).getResultList();
    }

    @Override
    public Price findLastByProduct(Integer productSkroutzId) {
        TypedQuery<Price> q = em.createNamedQuery("Price.findOrderedByProduct", Price.class);
        return q.setParameter("skroutzId", productSkroutzId).setMaxResults(1).getSingleResult();
    }

    @Override
    public List<Price> findBySku(Integer skuSkroutzId) {
        TypedQuery<Price> q = em.createNamedQuery("Price.findBySku", Price.class);
        return q.setParameter("skroutzId", skuSkroutzId).getResultList();
    }

    @Override
    public List<Price> findLastBySku(Integer skuSkroutzId) {
        return null;
    }

    @Override
    public List<Price> findAll() {
        TypedQuery<Price> q = em.createNamedQuery("Price.findAll", Price.class);
        return q.getResultList();
    }

    @Override
    public Price persist(Price price) {
        em.persist(price);
        return price;
    }
}
