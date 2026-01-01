package Repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPARepository {

    private final EntityManagerFactory emf;

    public JPARepository(String pUnit) {

        this.emf = Persistence.createEntityManagerFactory(pUnit);

    }

    public EntityManager createEntityManager() {

        return this.emf.createEntityManager();

    }

}
