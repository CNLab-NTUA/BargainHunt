package gr.ntua.cn.zannis.bargains.statistics;

/**
 * @author zannis <zannis.kal@gmail.com
 */

public enum Flexibility {
    RELAXED,
    NORMAL,
    STRONG;

    public Flexibility getStronger() {
        switch (this) {
            case NORMAL:
                return STRONG;
            case RELAXED:
                return NORMAL;
            case STRONG:
                return null;
            default:
                return null;
        }
    }

    public Flexibility getMoreFlexible() {
        switch (this) {
            case NORMAL:
                return RELAXED;
            case RELAXED:
                return null;
            case STRONG:
                return NORMAL;
            default:
                return null;
        }
    }

}
