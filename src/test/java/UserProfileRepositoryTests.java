import Entities.UserProfile;
import Repositories.UserProfileRepository;
import Utilities.Config;
import Utilities.Security.PasswordHasher;
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

        userRep = new UserProfileRepository(Config.getTestPersistenceUnit());

    }

    @Test
    public void createUserProfile() throws NoSuchAlgorithmException, InvalidKeySpecException {

        String hash = PasswordHasher.hashPassword(PASSWORD.toCharArray());
        UserProfile user = new UserProfile(USERNAME, hash);

        user = userRep.save(user);

        UserProfile savedUser = userRep.findByUsername(USERNAME);

        System.out.println(user);
        System.out.println(savedUser);

        assert savedUser != null;
        assert savedUser.equals(user);

    }

}
