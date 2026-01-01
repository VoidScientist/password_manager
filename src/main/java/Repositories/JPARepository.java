package Repositories;

import jakarta.persistence.EntityManager;

public class JPARepository {

    private final EntityManager em;

    public JPARepository(EntityManager entityManager) {

        this.em = entityManager;

    }

    public EntityManager getEntityManager() {

        return this.em;

    }


}
