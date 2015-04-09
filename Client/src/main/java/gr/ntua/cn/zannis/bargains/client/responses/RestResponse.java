package gr.ntua.cn.zannis.bargains.client.responses;

import gr.ntua.cn.zannis.bargains.client.persistence.SkroutzEntity;
import gr.ntua.cn.zannis.bargains.client.responses.meta.Meta;
import gr.ntua.cn.zannis.bargains.client.responses.meta.Page;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public interface RestResponse<T extends SkroutzEntity> {
    T getItem();
    List<T> getItems();
    Page<T> getPage();
    Meta getMeta();
}