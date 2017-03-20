package gr.ntua.cn.zannis.bargains.webapp.ejb;

import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Offer;
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

    public List<Offer> getTopActive(int offersToReturn) {
        List<Offer> results;

        TypedQuery<Offer> q = em.createNamedQuery(Offer.class.getSimpleName() + ".findActive", Offer.class);
        q.setMaxResults(offersToReturn);
        q.setParameter("someDate", Date.from(Instant.now().minus(5, ChronoUnit.DAYS)), TemporalType.DATE);
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
        em.persist(offer);
        return offer;
    }
}
