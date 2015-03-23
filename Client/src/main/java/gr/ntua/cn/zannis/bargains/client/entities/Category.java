package gr.ntua.cn.zannis.bargains.client.entities;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class Category {
    private long id;
    private long skroutzId;
    private String name;
    private int childrenCount;
    private String imageUrl;
    private long parentId;
    private boolean fashion;
    private List<Long> parentPath;
    private boolean showSpecifications;
    private String manufacturerTitle;
}
