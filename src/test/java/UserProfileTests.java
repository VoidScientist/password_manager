import Entities.UserProfile;
import Utilities.Security.PasswordHasher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class UserProfileTests {

    public static final String USERNAME = "TestUser987";
    public static final String PASSWORD = "test8/+";

    static EntityManagerFactory emf;

    @BeforeAll
    public static void init() throws NoSuchAlgorithmException, InvalidKeySpecException {

        emf = Persistence.createEntityManagerFactory("secur2i");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        String hash = PasswordHasher.hashPassword(PASSWORD.toCharArray());

        tx.begin();
        UserProfile user = new UserProfile(USERNAME, hash);

        em.persist(user);
        tx.commit();

    }

    @Test
    public void rightPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        UserProfile user = (UserProfile) em.createQuery("select u from UserProfile u where u.username = :username")
                .setParameter("username", USERNAME)
                .getSingleResult();
        tx.commit();

        assert user.connect(PASSWORD);

    }

    @Test
    public void wrongPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        UserProfile user = (UserProfile) em.createQuery("select u from UserProfile u where u.username = :username")
                .setParameter("username", USERNAME)
                .getSingleResult();
        tx.commit();

        assert !user.connect("ahahamhackeridon'tknowyourpassword12431554");

    }

}
