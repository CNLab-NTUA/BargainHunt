package gr.ntua.cn.zannis.bargains.client.persistence.dao.impl;

import gr.ntua.cn.zannis.bargains.client.impl.SkroutzRestClient;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.client.responses.impl.CategoryResponse;
import org.apache.commons.lang3.ArrayUtils;

/**
 * The custom {@link Category} data access object.
 * @author zannis <zannis.kal@gmail.com>
 */
public class CategoryData extends GenericSkroutzDaoImpl<Category> {

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
        Category currentCategory;
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
}
