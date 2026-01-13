import Entities.Category;
import Entities.Profile;
import Entities.UserProfile;
import Utilities.Config;
import Utilities.Security.PasswordHasher;
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
    public void persistUserProfile() throws NoSuchAlgorithmException, InvalidKeySpecException {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        String hash = PasswordHasher.hashPassword(PASSWORD.toCharArray());

        UserProfile user = new UserProfile(USERNAME, hash);

        em.persist(user);

        tx.commit();

        UserProfile savedUser = em.createQuery("SELECT u FROM UserProfile u WHERE u.username = :username", UserProfile.class)
                .setParameter("username", USERNAME)
                .getSingleResult();

        System.out.println(savedUser.toString());

    }

    @Test
    public void persistProfile() {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        Profile profile = new Profile("Amazon", USERNAME, PASSWORD, "amazon.com");
        em.persist(profile);
        tx.commit();

        Profile savedProfile = em.createQuery("SELECT p FROM Profile p WHERE p.service = :service", Profile.class)
                .setParameter("service", "Amazon")
                .getSingleResult();

        System.out.println(savedProfile.toString());

    }

    @Test
    public void persistCategory() {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        Category category = new Category("Shopping", "Comptes shopping en ligne");
        em.persist(category);
        tx.commit();

        Category savedCategory = em.createQuery("SELECT c FROM Category c WHERE c.name = :name", Category.class)
                .setParameter("name", "Shopping")
                .getSingleResult();

        System.out.println(savedCategory.toString());

    }

    @Test
    public void persistCategoryWithProfiles() {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        Category category = new Category("Shopping2", "Comptes shopping en ligne");
        em.persist(category);

        Profile profile1 = new Profile("Amazon2", USERNAME, PASSWORD, "amazon.com");
        Profile profile2 = new Profile("Steam", USERNAME, PASSWORD, "steam.com");
        em.persist(profile1);
        em.persist(profile2);

        category.addProfile(profile1);
        category.addProfile(profile2);

        tx.commit();

        Category savedCategory = em.createQuery("SELECT c FROM Category c WHERE c.name = :name", Category.class)
                .setParameter("name", "Shopping2")
                .getSingleResult();

        savedCategory.getProfiles().forEach(System.out::println);

        System.out.println(savedCategory);

    }

    @Test
    public void persistUserProfileWithData() throws NoSuchAlgorithmException, InvalidKeySpecException {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        String hash = PasswordHasher.hashPassword(PASSWORD.toCharArray());

        UserProfile user = new UserProfile(USERNAME+"1", hash);
        em.persist(user);

        Category category = new Category("Shopping3", "Comptes shopping en ligne");
        em.persist(category);

        Profile profile1 = new Profile("Amazon3", USERNAME, PASSWORD, "amazon.com");
        Profile profile2 = new Profile("Steam3", USERNAME, PASSWORD, "steam.com");
        em.persist(profile1);
        em.persist(profile2);

        category.addProfile(profile1);

        user.addCategory(category);
        user.addProfile(profile2);

        tx.commit();

        UserProfile savedUser = em.createQuery("SELECT u FROM UserProfile u WHERE u.username = :username", UserProfile.class)
                .setParameter("username", USERNAME+"1")
                .getSingleResult();

        System.out.println("User data:");
        System.out.println(savedUser.toString());

        System.out.println("\nUser categories:");
        savedUser.getCategories().forEach(cat -> {
            System.out.println(cat.toString());
            cat.getProfiles().forEach(System.out::println);
        });

        System.out.println("\nUser profiles:");
        savedUser.getProfiles().forEach(System.out::println);

    }

}

