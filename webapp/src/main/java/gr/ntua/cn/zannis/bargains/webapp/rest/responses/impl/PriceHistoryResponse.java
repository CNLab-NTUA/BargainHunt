package gr.ntua.cn.zannis.bargains.webapp.rest.responses.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.Sku;

/**
 * The wrapper class for {@link Category} related
 * requests.
 *
 * @author zannis <zannis.kal@gmail.com
 */
public class PriceHistoryResponse {

    private final Sku.PriceHistory history;

    public PriceHistoryResponse(@JsonProperty("history") Sku.PriceHistory history) {
        this.history = history;
    }

    public Sku.PriceHistory getHistory() {
        return history;
    }
}
