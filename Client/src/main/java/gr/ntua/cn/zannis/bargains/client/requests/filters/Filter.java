package gr.ntua.cn.zannis.bargains.client.requests.filters;

/**
 * Interface to represent the numerous filters that the Skroutz API offers.
 * @author zannis <zannis.kal@gmail.com>
 */
public interface Filter {
    String getName();
    String getValue();
    String getDefaultValue();
}
