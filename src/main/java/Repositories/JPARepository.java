package Repositories;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPARepository {

    private final EntityManagerFactory emf;

    public JPARepository(String pUnit) {

        this.emf = Persistence.createEntityManagerFactory(pUnit);

    }

}
