package Repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPARepository {

    private final EntityManager em;

    public JPARepository(EntityManager entityManager) {

        this.em = entityManager;

    }

    public EntityManager getEntityManager() {

        return this.em;

    }

}
