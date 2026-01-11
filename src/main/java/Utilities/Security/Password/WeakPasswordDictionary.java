package Utilities.Security.Password;

import Utilities.Config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public final class WeakPasswordDictionary {

    private static final Set<String> WEAK = new HashSet<>();
    private static boolean loaded = false;

    private WeakPasswordDictionary() {}

    /** Charge le dictionnaire UNE SEULE FOIS */
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

    /** Vérifie si le mot de passe est faible */
    public static boolean isWeak(String password) {
        load();
        return WEAK.contains(password.toLowerCase());
    }
}
