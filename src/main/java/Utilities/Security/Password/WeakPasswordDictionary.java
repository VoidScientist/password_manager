package Utilities.Security.Password;

import Utilities.Config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe permettant de charger un dictionnaire contenant des mots de passe faibles couramment utilisés.
 *
 * Charge une liste de mots de passe courants depuis le fichier badPasswords.txt
 * et permet de vérifier si un mot de passe donné en fait partie.
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 */
public final class WeakPasswordDictionary {

    // Set contenant tous les mots de passe faibles en minuscules
    private static final Set<String> WEAK = new HashSet<>();
    // Flag pour éviter les chargements multiples du dictionnaire
    private static boolean loaded = false;

    // Constructeur privé pour empêcher l'instanciation
    private WeakPasswordDictionary() {}

    /**
     * Charge le dictionnaire des mots de passe faibles depuis le fichier badPasswords.txt.
     *
     * Le fichier n'est chargé qu'une seule fois, même si la méthode est appelée plusieurs fois.
     *
     * Chaque ligne du fichier contient un mot de passe faible ou courant.
     *
     * @throws RuntimeException Si le fichier badPasswords.txt est introuvable ou illisible
     */
    public static void load() {
        if (loaded) return;

        try (InputStream is = WeakPasswordDictionary.class
                .getResourceAsStream("/badPasswords.txt");
             BufferedReader br = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty()) {
                    WEAK.add(line);
                }
            }

            loaded = true;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Impossible de charger badPasswords.txt", e
            );
        }
    }

    /**
     * Vérifie si un mot de passe fait partie du dictionnaire des mots de passe faibles.
     *
     * @param password Le mot de passe à vérifier
     * @return true si le mot de passe est dans le dictionnaire, false sinon
     */
    public static boolean isWeak(String password) {
        load();
        return WEAK.contains(password.toLowerCase());
    }
}
