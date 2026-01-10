import Entities.Category;
import Entities.Profile;
import Entities.UserProfile;
import Repositories.CategoryRepository;
import Repositories.ProfileRepository;
import Repositories.UserProfileRepository;
import Utilities.Config;
import Utilities.Security.PasswordHasher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class UserProfileRepositoryTests {

    private final String USERNAME = "TestUser987";
    private final String PASSWORD = "TestPassword8/+";

    private static UserProfileRepository userRep;
    private static ProfileRepository profRep;
    private static CategoryRepository catRep;

    @BeforeAll
    public static void init() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Config.getTestPersistenceUnit());
        EntityManager em = emf.createEntityManager();

        userRep = new UserProfileRepository(em);
        profRep = new ProfileRepository(em);
        catRep = new CategoryRepository(em);

    }

    @Test
    public void createThenRemoveUserProfile() throws NoSuchAlgorithmException, InvalidKeySpecException {

        String hash = PasswordHasher.hashPassword(PASSWORD.toCharArray());
        UserProfile user = new UserProfile(USERNAME, hash);

        userRep.getEntityManager().getTransaction().begin();

        user = userRep.save(user);

        userRep.getEntityManager().getTransaction().commit();

        UserProfile savedUser = userRep.findByUsername(USERNAME);

        System.out.println("Résultat sauvegarde / chargement (dans cet ordre)");
        System.out.println(user);
        System.out.println(savedUser);

        assert savedUser != null;
        assert savedUser.equals(user);

        userRep.getEntityManager().getTransaction().begin();

        userRep.delete(user);

        userRep.getEntityManager().getTransaction().commit();

        UserProfile savedUser2 = userRep.findByUsername(USERNAME);

        System.out.println("\nAprès suppression:");
        System.out.println(savedUser2);

        assert savedUser2 == null;

    }

    @Test
    public void createFictiveUser() throws NoSuchAlgorithmException, InvalidKeySpecException {

        String hash = PasswordHasher.hashPassword(PASSWORD.toCharArray());
        UserProfile user = new UserProfile(USERNAME + "Fictive", hash);

        Category shopping_cat =  new Category("Shopping", "Shopping en ligne");
        Category social_cat = new Category("Social", "Réseaux sociaux");

        Profile amazon_acc = new Profile("Amazon", USERNAME, PASSWORD, "amazon.com");
        Profile steam_acc = new Profile("Steam", USERNAME, PASSWORD, "steam.com");
        Profile bluesky_acc = new Profile("Bluesky",  USERNAME, PASSWORD, "bluesky.com");
        Profile netflix_acc = new Profile("Netflix", USERNAME, PASSWORD, "netflix.com");

        shopping_cat.addProfile(steam_acc);
        shopping_cat.addProfile(amazon_acc);
        social_cat.addProfile(bluesky_acc);

        user.addProfile(netflix_acc);

        user.addCategory(shopping_cat);
        user.addCategory(social_cat);


        userRep.getEntityManager().getTransaction().begin();

        user = userRep.save(user);

        userRep.getEntityManager().getTransaction().commit();



        UserProfile savedUser = userRep.findByUsername(USERNAME + "Fictive");

        assert savedUser != null;

        System.out.println("Initial user:");
        System.out.println(user);
        System.out.println("\nRetrieved user:");
        System.out.println(savedUser);
        System.out.println("\nCategories");
        savedUser.getCategories().forEach(System.out::println);;
        System.out.println("\nProfiles:");
        savedUser.getProfiles().forEach(System.out::println);



    }

}
