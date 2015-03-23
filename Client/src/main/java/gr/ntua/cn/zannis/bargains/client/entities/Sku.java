package gr.ntua.cn.zannis.bargains.client.entities;

import java.util.List;

/**
 * The persistent class for a collection of products.
 * @author zannis <zannis.kal@gmail.com
 */
public class Sku {
    private long id;
    private long skroutzId;
    private String ean;
    private String pn;
    private String name;
    private String displayName;
    private long categoryId;
    private String firstProductShopInfo;
    private String clickUrl;
    private float priceMax;
    private float priceMin;
    private float reviewScore;
    private int shopCount;
    private String plainSpecSummary;
    private long manufacturerId;
    private boolean future;
    private int reviewsCount;
    private boolean virtual;
    private List<String> images;
}
