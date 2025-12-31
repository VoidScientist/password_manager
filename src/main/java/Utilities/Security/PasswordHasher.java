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

    // fonction utilisée à l'inscription
    public static String hashPassword(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {

        // crée le salt du hash
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];

        random.nextBytes(salt);

        // prépare la KeySpec pour la création du secret
        KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

        // récupère le hash après avoir généré le secret
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // nettoie le mot de passe en mémoire (est-ce utile??)
        Arrays.fill(password, '\0');

        // renvoie un string en base 64 qui contient le salt et hash séparé par un $
        // FORMAT: [salt]$[hash]
        return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);

    }

    // fonction utilisée lors de la connexion
    public static String hashPasswordFromSalt(String b64_salt, char[] password) throws InvalidKeySpecException, NoSuchAlgorithmException {

        byte[] salt = Base64.getDecoder().decode(b64_salt);

        // prépare la KeySpec pour la création du secret
        KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

        // nettoie le mot de passe en mémoire (est-ce utile??)
        Arrays.fill(password, '\0');

        // récupère le hash après avoir généré le secret
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // renvoie simplement le hash en base64
        return Base64.getEncoder().encodeToString(hash);

    }

}
