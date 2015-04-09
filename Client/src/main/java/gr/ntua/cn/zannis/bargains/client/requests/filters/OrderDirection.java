package gr.ntua.cn.zannis.bargains.client.requests.filters;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public enum OrderDirection {
    ASCENDING("asc"),
    DESCENDING("desc");

    private String value;

    OrderDirection(final String value) {
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