package gr.ntua.cn.zannis.bargains.webapp.ejb;

import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Offer;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Price;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Sku;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com
 */

@Stateless
@LocalBean
public class OfferEntityManager {

    private static final Logger log = LoggerFactory.getLogger(SkroutzEntityManager.class);

    @PersistenceContext
    private EntityManager em;

    public List<Offer> getTopActive(int offersToReturn, Integer categorySkroutzId) {
        List<Offer> results;

        TypedQuery<Offer> q;
        if (categorySkroutzId != null) {
            q = em.createNamedQuery(Offer.class.getSimpleName() + ".findActiveByCategory", Offer.class);
            q.setParameter("categ_id", categorySkroutzId);
        } else {
            q = em.createNamedQuery(Offer.class.getSimpleName() + ".findActive", Offer.class);
        }
        q.setParameter("someDate", Date.from(Instant.now().minus(60, ChronoUnit.DAYS)), TemporalType.DATE);
        q.setMaxResults(offersToReturn);
        try {
            results = q.getResultList();
        } catch (NoResultException e) {
            results = Collections.emptyList();
            Notifier.error("Δε βρέθηκαν ενεργές προσφορές", e);
        } catch (Exception e) {
            results = Collections.emptyList();
            Notifier.error("Υπήρξε κάποιο πρόβλημα", e);
        }

        return results;
    }

    public Offer persist(Offer offer) {
        try {
            if (em.find(Price.class, offer.getPriceId()) == null) {
                em.persist(offer);
            } else {
                offer = em.merge(offer);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return offer;
    }

    public Offer findByProduct(Product product) {
        TypedQuery<Offer> q = em.createNamedQuery("Offer.findByProduct", Offer.class);
        q.setParameter("product_id", product.getId());
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return q.getResultList().get(0);
    }

    public Offer findBySku(Sku sku) {
        TypedQuery<Offer> q = em.createNamedQuery("Offer.findBySku", Offer.class);
        q.setParameter("sku_id", sku.getSkroutzId());
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return q.getResultList().get(0);
    }


    public Offer persistOrMerge(Offer offer) {
        Offer dbOffer = findByProduct(offer.getProduct());
        if (dbOffer != null) {
            dbOffer.setFinishedAt(Date.from(Instant.now()));
        }
        em.persist(offer);
        return offer;
    }
}
