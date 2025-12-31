import Entities.Profile;
import Utilities.Config;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class JPATests {

    public static final String USERNAME = "TestUser987";
    public static final String PASSWORD = "test8/+";

    static EntityManagerFactory emf;

    @BeforeAll
    public static void init() {

        emf = Persistence.createEntityManagerFactory(Config.getTestPersistenceUnit());

    }

    @Test
    public void persistUserProfile() {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();


    }



}

