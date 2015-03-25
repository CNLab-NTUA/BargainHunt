package gr.ntua.cn.zannis.bargains.client.dto;

import gr.ntua.cn.zannis.bargains.client.dto.meta.Meta;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Page;
import gr.ntua.cn.zannis.bargains.client.persistence.PersistentEntity;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public interface RestResponse<T extends PersistentEntity> {
    T getItem();
    List<T> getItems();
    Page<T> getPage();
    Meta getMeta();
}