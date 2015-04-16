package gr.ntua.cn.zannis.bargains.client.requests.filters;

/**
 * Order By filter implementation.
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

    @Override
    public String getName() {
        return "order_by";
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDefaultValue() {
        return POPULARITY.getValue();
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}