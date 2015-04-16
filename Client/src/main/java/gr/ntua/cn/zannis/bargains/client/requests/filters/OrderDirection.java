package gr.ntua.cn.zannis.bargains.client.requests.filters;

/**
 * Order Direction filter implementation.
 * @author zannis <zannis.kal@gmail.com>
 */
public enum OrderDirection implements Filter {
    ASCENDING("asc"),
    DESCENDING("desc");

    private String value;

    OrderDirection(final String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return "order_dir";
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDefaultValue() {
        return DESCENDING.getValue();
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}