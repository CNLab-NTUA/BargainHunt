package gr.ntua.cn.zannis.bargains.webapp.ejb.dao;

import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Request;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public interface RequestDAO {

    Request findByUrl(String url);

    Request findByEtag(String etag);

    List<Request> findAll();

    Request saveOrUpdate(Request request);
}
