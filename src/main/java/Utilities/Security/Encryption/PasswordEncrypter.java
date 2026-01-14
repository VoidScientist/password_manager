package Utilities.Security.Encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;

import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncrypter {

    private static final String ALGORITHM = "AES";
    private static final String ALG_MOD_PAD = "AES/GCM/NoPadding";

    public static String encrypt(String password, String hashedMasterPassword) {

        try {

            SecretKeySpec key = getEncryptionKey(hashedMasterPassword);

            Cipher cipher = Cipher.getInstance(ALG_MOD_PAD);

            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    key,
                    new GCMParameterSpec(128, iv)
            );


            byte[] cipherbytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));

            String ciphertext = Base64.getEncoder().encodeToString(cipherbytes);
            String b64IV = Base64.getEncoder().encodeToString(iv);

            return b64IV + "$" + ciphertext;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static String decrypt(String encryptedPassword, String hashedMasterPassword) {

        try {

            SecretKeySpec key = getEncryptionKey(hashedMasterPassword);

            String[] parts = encryptedPassword.split("\\$");

            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] cypherbytes = Base64.getDecoder().decode(parts[1]);

            Cipher cipher = Cipher.getInstance(ALG_MOD_PAD);

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    key,
                    new GCMParameterSpec(128, iv)
            );

            byte[] passwordbytes = cipher.doFinal(cypherbytes);

            return new String(passwordbytes, StandardCharsets.UTF_8);

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

    private static SecretKeySpec getEncryptionKey(String hashedPassword) {
        String hash = hashedPassword.split("\\$")[1];

        byte[] hashBytes = Base64.getDecoder().decode(hash);

        return new SecretKeySpec(hashBytes, ALGORITHM);
    }


}
