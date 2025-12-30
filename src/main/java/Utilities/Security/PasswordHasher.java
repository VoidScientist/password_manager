package Utilities.Security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher {

    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 128;
    private static final int SALT_LENGTH = 16;

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    // used at register time
    public static String hashPassword(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {

        // creates the salt for the hash
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];

        random.nextBytes(salt);

        // prepares the PBEKeySpec to generate the secret
        KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

        // retrieve the hash
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // clear password content in memory
        Arrays.fill(password, '\0');

        // return HashInfo record class to be used by caller
        return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);

    }

    // used at login time
    public static String hashPasswordFromSalt(String b64_salt, char[] password) throws InvalidKeySpecException, NoSuchAlgorithmException {

        byte[] salt = Base64.getDecoder().decode(b64_salt);

        // prepares the PBEKeySpec to generate the secret
        KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

        // clear password in memory
        Arrays.fill(password, '\0');

        // computes the hash
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // return the hash as b64 string
        return Base64.getEncoder().encodeToString(hash);

    }

}
