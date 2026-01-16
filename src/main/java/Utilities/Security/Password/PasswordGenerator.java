package Utilities.Security.Password;

import java.security.SecureRandom;

/**
 * Classe utilitaire pour générer des mots de passe aléatoires.
 * Permet de personnaliser les caractères utilisés (majuscules, chiffres, symboles)
 * et d'exclure les caractères ambigus (l, I, O, 0, 1).
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 */

public class PasswordGenerator {

    private static final SecureRandom random = new SecureRandom();

    /**
     * Génère un mot de passe selon les critères spécifiés.
     *
     * @param length Longueur du mot de passe à générer (minimum 1)
     * @param useUppercase Si true, inclut des lettres majuscules
     * @param useDigits Si true, inclut des chiffres
     * @param useSymbols Si true, inclut des symboles
     * @param allowAmbiguous Si false, exclut les caractères ambigus (l, I, O, 0, 1)
     * @return Le mot de passe généré
     * @throws IllegalArgumentException Si length < 1 ou si aucun caractère n'est disponible
     */
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
            // Exclure 'l' et 'o'
            lowercase = lowercase.replace("l", "").replace("o", "");
        }
        pool.append(lowercase);

        // Majuscules
        if (useUppercase) {
            String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            if (!allowAmbiguous) {
                // Exclure 'I' et 'O'
                uppercase = uppercase.replace("I", "").replace("O", "");
            }
            pool.append(uppercase);
        }

        // Chiffres
        if (useDigits) {
            String digits = "0123456789";
            if (!allowAmbiguous) {
                // Exclure '1' et '0'
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