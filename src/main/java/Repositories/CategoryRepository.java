package Repositories;

import Entities.Category;
import Repositories.Interface.ISecureRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class CategoryRepository extends JPARepository<Category> implements ISecureRepository<Category, Integer> {

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

}
