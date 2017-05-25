package gr.ntua.cn.zannis.bargains.webapp.ejb.dao;

import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Offer;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public interface OfferDAO {
    List<Offer> findTopActive(int offersToReturn, int categoryId);
}
