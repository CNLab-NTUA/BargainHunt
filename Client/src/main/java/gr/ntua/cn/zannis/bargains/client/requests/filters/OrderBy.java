package gr.ntua.cn.zannis.bargains.client.requests.filters;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public enum OrderBy implements Filter {
    NAME("name"),
    PRICE("price"),
    POPULARITY("popularity");

    private String value;

    OrderBy(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}