import Utilities.Security.PasswordHasher;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class PasswordHasherTests {

    final private String TEST_PASSWORD = "Ilovepoo<3";

    @Test
    public void ResultCoherence() throws InvalidKeySpecException, NoSuchAlgorithmException {

        String result = PasswordHasher.hashPassword(TEST_PASSWORD.toCharArray());

        String[] tmp = result.split("\\$");
        String salt = tmp[0];
        String hash = tmp[1];

        String login_hash = PasswordHasher.hashPasswordFromSalt(salt, TEST_PASSWORD.toCharArray());

        System.out.println(
                "Register: " + login_hash
                + "\nLog in: " + login_hash
        );

        assertEquals(login_hash, hash);

    }

    @Test
    public void HashDifferenceSamePassword() throws InvalidKeySpecException, NoSuchAlgorithmException {

        String result = PasswordHasher.hashPassword(TEST_PASSWORD.toCharArray());
        String result2 = PasswordHasher.hashPassword(TEST_PASSWORD.toCharArray());

        System.out.println(
                "Result 1: " + result
                + "\nResult 2: " + result2
        );

        assertNotEquals(result, result2);

    }

}
