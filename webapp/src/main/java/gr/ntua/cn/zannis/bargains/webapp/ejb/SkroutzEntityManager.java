package gr.ntua.cn.zannis.bargains.webapp.ejb;

import gr.ntua.cn.zannis.bargains.webapp.persistence.SkroutzEntity;
import gr.ntua.cn.zannis.bargains.webapp.persistence.entities.*;
import gr.ntua.cn.zannis.bargains.webapp.rest.impl.SkroutzClient;
import gr.ntua.cn.zannis.bargains.webapp.rest.responses.meta.Meta;
import gr.ntua.cn.zannis.bargains.webapp.ui.components.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
@Stateless
@Local
@TransactionManagement
public class SkroutzEntityManager {

    private static final Logger log = LoggerFactory.getLogger(SkroutzEntityManager.class);

    @PersistenceContext
    private EntityManager em;


    public <T extends SkroutzEntity> T persist(T object) throws RuntimeException {

        try {
            em.persist(object);
            log.info(object.getClass().getSimpleName() + " " + object.getSkroutzId() + " persisted successfully.");
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο persist στο " + object.toString(), e);
        }
        return object;
    }

    public <T extends SkroutzEntity> T merge(T object) throws RuntimeException {
        try {
            em.merge(object);
            log.info(object.getClass().getSimpleName() + " " + object.getSkroutzId() + " merged successfully.");
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο merge στο " + object.toString(), e);
        }
        return object;
    }

    public <T extends SkroutzEntity> void remove(T object) throws RuntimeException {
        try {
            em.remove(object);
            log.info("Object " + object + " removed successfully.");
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο remove στο " + object.toString(), e);
        }
    }

    public <T extends SkroutzEntity> T find(Class<T> tClass, Integer skroutzId) throws RuntimeException {
        T result;
        // finds an object by its skroutzId
        TypedQuery<T> q = em.createNamedQuery(tClass.getSimpleName() + ".findBySkroutzId", tClass);
        q.setParameter("skroutzId", skroutzId);
        q.setMaxResults(1);
        try {
            if (!q.getResultList().isEmpty()) {
                result = q.getResultList().get(0);
            } else {
                result = null;
            }
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο find" + tClass.getSimpleName() + " με skroutzId " + skroutzId, e);
            result = null;
        }
        return result;
    }

    public <T extends SkroutzEntity> List<T> findAll(Class<T> tClass) throws RuntimeException {
        List<T> result;
        TypedQuery<T> q = createNamedQuery(tClass.getSimpleName() + ".findAll", tClass);
        try {
            result = q.getResultList();
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο findAll" + tClass.getSimpleName(), e);
            result = new ArrayList<>();
        }
        return result;
    }

    public <T extends SkroutzEntity> List<T> findParsed(Class<T> tClass, int... ids) throws RuntimeException {
        List<T> result;
        TypedQuery<T> q = createNamedQuery(tClass.getSimpleName() + ".findCrawled", tClass);
        if (ids.length == 1) {
            q.setParameter("categ_id", ids[0]);
        }
        try {
            result = q.getResultList();
        } catch (Exception e) {
            Notifier.error("Υπήρξε πρόβλημα στο findParsed" + tClass.getSimpleName(), e);
            result = new ArrayList<>();
        }
        return result;
    }

    private <T extends SkroutzEntity> TypedQuery<T> createNamedQuery(String namedQuery, Class<T> tClass) {
        return em.createNamedQuery(namedQuery, tClass);
    }

    public <T extends SkroutzEntity> List<T> persistOrMerge(Class<T> tClass, List<T> objects) {
        List<T> results = new LinkedList<>();
        if (objects != null) {
            for (T object : objects) {
                results.add(persistOrMerge(tClass, object));
            }
        }
        return results;
    }

    public <T extends SkroutzEntity> T persistOrMerge(Class<T> tClass, T transientObject) {
        if (tClass != null & transientObject != null) {
            T persistentObject = find(tClass, transientObject.getSkroutzId());
            boolean success = true;
            if (persistentObject == null) {
                if (transientObject.getClass().isAssignableFrom(Product.class)) {
                    // the transient object contains the fields shop_id, category_id, sku_id
                    // persist its shop if not already there. sku and category must have been persisted before this call
                    Product transientProduct = (Product) transientObject;
                    Shop shop = find(Shop.class, transientProduct.getShopId());
                    if (shop == null) {
                        shop = SkroutzClient.getInstance().get(Shop.class, transientProduct.getShopId());
                        em.persist(shop);
                        if (shop.getProducts() == null) {
                            shop.setProducts(new ArrayList<>());
                        }
                        em.flush();
                    }
                    shop.getProducts().add(transientProduct);
                    transientProduct.setShop(shop);
//                    em.persist(transientProduct);
                    em.flush();
                    Price price = Price.fromProduct(transientProduct);
                    List<Price> list = new ArrayList<>();
                    list.add(price);
                    transientProduct.setPrices(list);
                    em.flush();
                    success = false; // hack to make cascading persist work
                } else if (transientObject.getClass().isAssignableFrom(Sku.class)) {
                    Sku transientSku = (Sku) transientObject;
                    Category category = SkroutzClient.getInstance().get(Category.class, transientSku.getCategoryId());
                    persistOrMerge(Category.class, category);
                } else if (transientObject.getClass().isAssignableFrom(Category.class)) {
                    List<Category> categories = findAll(Category.class);
                    Category transientCategory = (Category) transientObject;
                    String[] temp = transientCategory.getPath().split(",");
                    for (String parentIdString : Arrays.copyOf(temp, temp.length - 1)) {
                        if (Integer.parseInt(parentIdString) != 0 &&
                                categories.stream().noneMatch((c) -> c.getSkroutzId() == Integer.parseInt(parentIdString))) {
                            Category parent = SkroutzClient.getInstance().get(Category.class, Integer.parseInt(parentIdString));
                            if (parent != null) {
                                categories.add(parent);
                            } else {
                                success = false;
                                break;
                            }
                        }
                    }
                    persistOrMerge(Category.class, categories);
                }
                if (success) {
                    persistentObject = persist(transientObject);
                } else {
                    persistentObject = transientObject;
                }
            } else {
                persistentObject.updateFrom(transientObject);
                merge(persistentObject);
            }
            return persistentObject;
        } else {
            return null;
        }
    }

    public void persistOrMergeStrongMatches(Meta.StrongMatches strongMatches) {
        if (strongMatches.hasCategory()) {
            persistOrMerge(Category.class, strongMatches.getCategory());
        } else if (strongMatches.hasManufacturer()) {
            persistOrMerge(Manufacturer.class, strongMatches.getManufacturer());
        } else if (strongMatches.hasShop()) {
            persistOrMerge(Shop.class, strongMatches.getShop());
        } else if (strongMatches.hasSku()) {
            persistOrMerge(Sku.class, strongMatches.getSku());
        }
    }

    public void flush() {
        em.flush();
    }

    public Collection<Product> findProductsForSku(int skroutzId) {
        TypedQuery<Product> q = em.createNamedQuery("Product.findAllBySku", Product.class);
        q.setParameter("sku", skroutzId);
        return q.getResultList();
    }

//    public void initMissingPrices() {
//        TypedQuery<Sku> q = em.createNamedQuery("Sku.findAllByCategory", Sku.class);
//        q.setParameter("categ_id", 1705);
//        for (Sku s : q.getResultList()) {
//            for (Product p : s.getProducts()) {
//                if (p.getPrice() > 0 && p.getPrices().isEmpty()) {
//                    p.getPrices().add(Price.fromProduct(p));
//                    em.merge(p);
//                } else {
//                    log.info("price 0! -> " + p.toString());
//                }
//            }
//        }
//    }
}
