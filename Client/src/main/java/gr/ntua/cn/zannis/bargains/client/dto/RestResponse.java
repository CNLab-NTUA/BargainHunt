package gr.ntua.cn.zannis.bargains.client.dto;

import gr.ntua.cn.zannis.bargains.client.dto.meta.Meta;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public interface RestResponse<T> {
    public T getItem();
    public T getItems();
    public Meta getMeta();
}
