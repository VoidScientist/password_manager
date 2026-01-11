package Utilities.Security.Password;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generate(int length, boolean useUppercase, boolean useDigits,
                                  boolean useSymbols, boolean allowAmbiguous) {
        if (length < 1) {
            throw new IllegalArgumentException("Longueur minimale : 1");
        }

        // Construire le pool de caractères
        StringBuilder pool = new StringBuilder();

        // Minuscules (toujours incluses)
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        if (!allowAmbiguous) {
            lowercase = lowercase.replace("l", "").replace("o", "");
        }
        pool.append(lowercase);

        // Majuscules
        if (useUppercase) {
            String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            if (!allowAmbiguous) {
                uppercase = uppercase.replace("I", "").replace("O", "");
            }
            pool.append(uppercase);
        }

        // Chiffres
        if (useDigits) {
            String digits = "0123456789";
            if (!allowAmbiguous) {
                digits = digits.replace("0", "").replace("1", "");
            }
            pool.append(digits);
        }

        // Symboles
        if (useSymbols) {
            pool.append("!@#$%^&*()-_=+[]{};:,.<>?");
        }

        if (pool.length() == 0) {
            throw new IllegalArgumentException("Aucun caractère disponible");
        }

        // Générer le mot de passe
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(pool.charAt(random.nextInt(pool.length())));
        }

        return password.toString();
    }
}