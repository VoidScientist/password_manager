import Entities.UserProfile;
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

    @BeforeAll
    public static void init() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Config.getTestPersistenceUnit());
        EntityManager em = emf.createEntityManager();

        userRep = new UserProfileRepository(em);

    }

    @Test
    public void createThenRemoveUserProfile() throws NoSuchAlgorithmException, InvalidKeySpecException {

        String hash = PasswordHasher.hashPassword(PASSWORD.toCharArray());
        UserProfile user = new UserProfile(USERNAME, hash);

        user = userRep.save(user);

        UserProfile savedUser = userRep.findByUsername(USERNAME);

        System.out.println("Résultat sauvegarde / chargement (dans cet ordre)");
        System.out.println(user);
        System.out.println(savedUser);

        assert savedUser != null;
        assert savedUser.equals(user);

        userRep.delete(user);

        UserProfile savedUser2 = userRep.findByUsername(USERNAME);

        System.out.println("\nAprès suppression:");
        System.out.println(savedUser2);

        assert savedUser2 == null;

    }

}
