package gr.ntua.cn.zannis.bargains.client.persistence.dao;

import gr.ntua.cn.zannis.bargains.client.dto.impl.CategoryResponse;
import gr.ntua.cn.zannis.bargains.client.impl.SkroutzRestClient;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Category;
import org.apache.commons.lang3.ArrayUtils;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * The custom {@link Category} data access object.
 * @author zannis <zannis.kal@gmail.com>
 */
public class CategoryData extends GenericDaoImpl<Category> {

    public CategoryData() {
        super(Category.class);
    }

    /**
     * We persist a category entity that contains the fields taken from the {@link CategoryResponse}
     * and we use its parentPath field to persist all its transient parents before persisting itself so that
     * we create valid parent-child relations.
     * @param category The transient {@link Category} entity.
     * @return The persistent entity.
     */
    @Override
    public Category persist(Category category) {
        Category parentCategory = null;
        Category currentCategory = null;
        // we parse its parents first
        String[] path = category.getParentPath().split(",");
        String[] parents = ArrayUtils.subarray(path, 0, path.length - 1);
        for (String parentId : parents) {
            currentCategory = findBySkroutzId(Long.parseLong(parentId));
            if (currentCategory == null) {
                currentCategory = SkroutzRestClient.get().getCategoryById(Long.parseLong(parentId));
                currentCategory.setParent(parentCategory);
                em.getTransaction().begin();
                em.persist(currentCategory);
                em.getTransaction().commit();
            }
            parentCategory = currentCategory;
        }
        // add the category if it doesn't already exist
        currentCategory = findBySkroutzId(category.getSkroutzId());
        if (currentCategory == null) {
            category.setParent(parentCategory);
            em.getTransaction().begin();
            em.persist(category);
            em.getTransaction().commit();
            return category;
        } else {
            return currentCategory;
        }
    }

    @Override
    public Category find(long id) {
        return super.find(id);
    }

    public Category findBySkroutzId(long id) {
        Category category = null;
        try {
            em.getTransaction().begin();
            TypedQuery<Category> q = em.createNamedQuery("Category.findBySkroutzId", Category.class);
            q.setParameter("skroutzId", id);
            category = q.getSingleResult();
        } catch (NoResultException e) {
            //swallow and return null
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
        return category;
    }
}
