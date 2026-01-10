package Repositories;

import jakarta.persistence.EntityManager;

public class JPARepository<ModelType> {

    private final EntityManager em;

    public JPARepository(EntityManager entityManager) {

        this.em = entityManager;

    }

    public EntityManager getEntityManager() {

        return this.em;

    }

    public ModelType save(ModelType model) {

        EntityManager em = this.getEntityManager();

        if (em.contains(model)) {
            model = em.merge(model);
        } else {
            em.persist(model);
        }

        return model;

    }

    public void delete(ModelType model) {

        EntityManager em = this.getEntityManager();

        if (em.contains(model)) {
            em.remove(model);
        }

    }

}
