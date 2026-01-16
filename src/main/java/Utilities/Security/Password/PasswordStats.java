package Utilities.Security.Password;

/**
 * Classe utilitaire pour analyser les caractères d'un mot de passe.
 * Calcule le nombre de caractères de chaque type (minuscules, majuscules, chiffres, symboles).
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 */

public class PasswordStats {

    private int length;     // Longueur totale
    private int lowercase;  // Nombre de minuscules
    private int uppercase;  // Nombre de maujuscules
    private int digits;     // Nombre de chiffres
    private int symbols;    // Nombre de symboles

    /**
     * Analyse le mot de passe en paramètre.
     * Compte le nombre de minuscules, majuscules, chiffres et symboles.
     *
     * @param password Le mot de passe à analyser
     */
    public PasswordStats(String password) {
        this.length = password.length();
        this.lowercase = 0;
        this.uppercase = 0;
        this.digits = 0;
        this.symbols = 0;

        // Parcourir chaque caractère et incrémenter le compteur correspondant
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) lowercase++;
            else if (Character.isUpperCase(c)) uppercase++;
            else if (Character.isDigit(c)) digits++;
            else symbols++;
        }
    }

    public int getLength() { return length; }
    public int getLowercase() { return lowercase; }
    public int getUppercase() { return uppercase; }
    public int getDigits() { return digits; }
    public int getSymbols() { return symbols; }
}