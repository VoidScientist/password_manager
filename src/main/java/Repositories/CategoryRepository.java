package Repositories;

import Entities.Category;
import Repositories.Interface.ISecureRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

import java.util.List;

public class CategoryRepository extends JPARepository implements ISecureRepository<Category, Integer> {

    public CategoryRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public Category findById(Integer id, String uuid) {
        try {
            return this.getEntityManager()
                    .createQuery("SELECT c FROM Category c WHERE c.owner.uuid = :uuid AND c.id = :id", Category.class)
                    .setParameter("uuid", uuid)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Category> readAll(String uuid) {
        return this.getEntityManager()
                .createQuery("SELECT c FROM Category c WHERE c.owner.uuid = :uuid", Category.class)
                .setParameter("uuid", uuid)
                .getResultList();
    }

    @Override
    public Category save(Category model) {

        EntityManager em = this.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();
            model = em.merge(model);
            tx.commit();

            return model;

        } catch (Exception e) {

            if (tx.isActive()) tx.rollback();
            throw e;

        }

    }

    @Override
    public void delete(Category model) {

        EntityManager em = this.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        int id = model.getId();

        try {

            tx.begin();
            Category category = em.find(Category.class, id);

            if (category != null) {
                em.remove(category);
            }

            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) tx.rollback();
            throw e;

        }


    }
}
