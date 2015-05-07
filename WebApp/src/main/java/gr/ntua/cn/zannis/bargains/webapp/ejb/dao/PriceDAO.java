package gr.ntua.cn.zannis.bargains.webapp.ejb.dao;

import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Price;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public interface PriceDAO {

    List<Price> findByProduct(Integer productSkroutzId);

    Price findLastByProduct(Integer productSkroutzId);

    List<Price> findBySku(Integer skuSkroutzId);

    List<Price> findLastBySku(Integer skuSkroutzId);

    List<Price> findAll();

    Price persist(Price price);
}
