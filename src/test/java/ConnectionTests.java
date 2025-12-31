import Entities.UserProfile;
import Utilities.Config;
import Repositories.UserProfileRepository;
import Utilities.Security.PasswordHasher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class ConnectionTests {

    public static final String USERNAME = "TestUser987";
    public static final String PASSWORD = "test8/+";

    static UserProfileRepository userRep;

    @BeforeAll
    public static void init() throws NoSuchAlgorithmException, InvalidKeySpecException {

        userRep = new UserProfileRepository(Config.getTestPersistenceUnit());

        String hash = PasswordHasher.hashPassword(PASSWORD.toCharArray());

        UserProfile user = new UserProfile(USERNAME, hash);

        userRep.save(user);

    }

    @Test
    public void rightPassword() {

        UserProfile user = userRep.findByUsername(USERNAME);

        System.out.println(user.toString());

        //TODO REIMPLEMENT

    }

    @Test
    public void wrongPassword() {

        UserProfile user = userRep.findByUsername(USERNAME);

        System.out.println(user.toString());

        //TODO REIMPLEMENT

    }

}
