package gr.ntua.cn.zannis.bargains.webapp.client.requests.filters;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class QueryFilter implements Filter {

    private String value;

    public QueryFilter(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return "q";
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDefaultValue() {
        return "";
    }
}
