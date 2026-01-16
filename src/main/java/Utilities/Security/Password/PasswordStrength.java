package Utilities.Security.Password;

import java.util.HashSet;
import java.util.Set;

/**
 * Classe d'analyse de la robustesse d'un mot de passe.
 * Applique plusieurs pénalités pour détecter les mots de passe faibles :
 * séquences, manque de diversité, répétitions, mots courants du dictionnaire.
 * <p>
 * Le niveau de force est calculé de 1 (Très faible) à 5 (Excellent).
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 */
public class PasswordStrength {
    private double entropy;

    private int level;

    /**
     * Analyse un mot de passe et calcule son niveau de robustesse.
     *
     * @param password Le mot de passe à analyser
     */
    public PasswordStrength(String password) {
        this.entropy = calculateEntropy(password);
        this.level = calculateLevel(this.entropy);
    }

    public double getEntropy() {
        return entropy;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        // Retourne la description textuelle du niveau de robustesse
        switch (level) {
            case 1: return "Très faible";
            case 2: return "Faible";
            case 3: return "Moyen";
            case 4: return "Fort";
            case 5: return "Excellent";
            default: return "Inconnu";
        }
    }

    /**
     * Calcule l'entropie du mot de passe en bits.
     *
     * @param password Le mot de passe à analyser
     * @return L'entropie en bits
     */
    private double calculateEntropy(String password) {
        int pool = calculateCharacterPool(password);
        // Formule de Shannon: entropie = longueur × log2(taille du pool)
        double entropy = password.length() * (Math.log(pool) / Math.log(2));

        // Appliquer les pénalités
        entropy -= sequencePenalty(password);
        entropy -= diversityPenalty(password);
        entropy -= singleTypePenalty(password);
        entropy -= repetitionPenalty(password);

        // Vérifier le dictionnaire des mots de passe faibles
        if (WeakPasswordDictionary.isWeak(password)) {
            entropy -= 60;
        }

        return Math.max(0, entropy);
    }

    /**
     * Calcule la taille du pool de caractères utilisés dans le mot de passe.
     *
     * Pools possibles :
     * - Minuscules (a-z): 26 caractères
     * - Majuscules (A-Z): 26 caractères
     * - Chiffres (0-9): 10 caractères
     * - Symboles: 25 caractères
     *
     * @param password Le mot de passe à analyser
     * @return La taille totale du pool
     */
    private int calculateCharacterPool(String password) {
        // Détecter les types de caractères présents
        boolean l=false, u=false, d=false, s=false;     // l: minuscule | u: majuscule | d: chiffre | s: symbole
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) l=true;
            else if (Character.isUpperCase(c)) u=true;
            else if (Character.isDigit(c)) d=true;
            else s=true;
        }
        int pool = 0;
        if (l) pool += 26;
        if (u) pool += 26;
        if (d) pool += 10;
        if (s) pool += 25;
        return pool;
    }

    /**
     * Calcule la pénalité pour les séquences de caractères consécutifs.
     *
     * Détecte les suites croissantes (abc, 123) ou décroissantes (cba, 321).
     * Pénalité: 8 bits par séquence de 3 caractères consécutifs.
     *
     * @param password Le mot de passe à analyser
     * @return La pénalité totale en bits
     */
    private double sequencePenalty(String password) {
        double penalty = 0;
        String lower = password.toLowerCase();

        for (int i = 2; i < password.length(); i++) {
            char a = password.charAt(i-2);
            char b = password.charAt(i-1);
            char c = password.charAt(i);

            // Détecter suites croissantes (abc, 123) ou décroissantes (cba, 321)
            if (b == a + 1 && c == b + 1) penalty += 8; // Croissante
            if (b == a - 1 && c == b - 1) penalty += 8; // Décroissante
        }

        return penalty;
    }

    /**
     * Calcule la pénalité pour le manque de diversité dans les caractères.
     * Mesure le ratio de caractères uniques par rapport à la longueur totale.
     *
     * @param password Le mot de passe à analyser
     * @return La pénalité en bits
     */
    private double diversityPenalty(String password) {
        // Compter les caractères uniques
        Set<Character> unique = new HashSet<>();
        for (char c : password.toCharArray()) unique.add(c);
        // Calculer le ratio: caractères uniques / longueur totale
        double ratio = (double) unique.size() / password.length();
        if (ratio < 0.4) return 30;
        if (ratio < 0.5) return 25;
        if (ratio < 0.6) return 18;
        if (ratio < 0.7) return 12;
        return 0;
    }

    /**
     * Calcule la pénalité si le mot de passe n'utilise qu'un seul type de caractères.
     * Pénalité : 30 bits si un seul type est utilisé.
     *
     * @param password Le mot de passe à analyser
     * @return 30 si un seul type, 0 sinon
     */
    private double singleTypePenalty(String password) {
        int types = 0;
        if (password.matches(".*[a-z].*")) types++;
        if (password.matches(".*[A-Z].*")) types++;
        if (password.matches(".*[0-9].*")) types++;
        if (password.matches(".*[^a-zA-Z0-9].*")) types++;

        return (types == 1) ? 30 : 0;
    }

    /**
     * Calcule la pénalité pour les caractères répétés consécutivement.
     * Détecte les répétitions de 3 caractères ou plus.
     * Pénalité : 10 bits par groupe de répétitions.
     *
     * @param password Le mot de passe à analyser
     * @return La pénalité totale en bits
     */
    private double repetitionPenalty(String password) {
        double penalty = 0;
        int count = 1;

        for (int i = 1; i < password.length(); i++) {
            if (password.charAt(i) == password.charAt(i - 1)) {
                count++;    // Incrémenter le compteur de répétitions
            } else {
                if (count >= 3) penalty += 10;  // Appliquer la pénalité (>3)
                count = 1;  // Reset du compteur
            }
        }
        // Vérifier la dernière séquence
        if (count >= 3) penalty += 10;

        return penalty;
    }

    /**
     * Convertit l'entropie en niveau de robustesse.
     *
     * @param entropy L'entropie calculée en bits
     * @return Le niveau de robustesse (1-5)
     */
    private int calculateLevel(double entropy) {
        if (entropy < 28) return 1;     // Très faible
        if (entropy < 50) return 2;     // Faible
        if (entropy < 65) return 3;     // Moyen
        if (entropy < 80) return 4;     // Fort
        return 5;                       // Très Fort
    }
}