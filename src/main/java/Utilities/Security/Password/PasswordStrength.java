package Utilities.Security.Password;

import java.util.HashSet;
import java.util.Set;

public class PasswordStrength {

    private double entropy;
    private int level;

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
        switch (level) {
            case 1: return "Très faible";
            case 2: return "Faible";
            case 3: return "Moyen";
            case 4: return "Fort";
            case 5: return "Excellent";
            default: return "Inconnu";
        }
    }

    private double calculateEntropy(String password) {
        int pool = calculateCharacterPool(password);
        double entropy = password.length() * (Math.log(pool) / Math.log(2));

        // Appliquer les pénalités
        entropy -= sequencePenalty(password);
        entropy -= diversityPenalty(password);
        entropy -= singleTypePenalty(password);
        entropy -= repetitionPenalty(password);

        // IMPORTANT: Vérifier le dictionnaire des mots de passe faibles
        if (WeakPasswordDictionary.isWeak(password)) {
            entropy -= 60;
        }

        return Math.max(0, entropy);
    }

    private int calculateCharacterPool(String password) {
        boolean l=false, u=false, d=false, s=false;
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
        if (s) pool += 32;
        return pool;
    }

    private double sequencePenalty(String password) {
        double penalty = 0;
        String lower = password.toLowerCase();

        for (int i = 2; i < password.length(); i++) {
            char a = password.charAt(i-2);
            char b = password.charAt(i-1);
            char c = password.charAt(i);

            if (b == a + 1 && c == b + 1) penalty += 8;
            if (b == a - 1 && c == b - 1) penalty += 8;
        }

        return penalty;
    }

    private double diversityPenalty(String password) {
        Set<Character> unique = new HashSet<>();
        for (char c : password.toCharArray()) unique.add(c);

        double ratio = (double) unique.size() / password.length();
        if (ratio < 0.4) return 30;
        if (ratio < 0.5) return 25;
        if (ratio < 0.6) return 18;
        if (ratio < 0.7) return 12;
        return 0;
    }

    private double singleTypePenalty(String password) {
        int types = 0;
        if (password.matches(".*[a-z].*")) types++;
        if (password.matches(".*[A-Z].*")) types++;
        if (password.matches(".*[0-9].*")) types++;
        if (password.matches(".*[^a-zA-Z0-9].*")) types++;

        return (types == 1) ? 30 : 0;
    }

    private double repetitionPenalty(String password) {
        double penalty = 0;
        int count = 1;

        for (int i = 1; i < password.length(); i++) {
            if (password.charAt(i) == password.charAt(i - 1)) {
                count++;
            } else {
                if (count >= 3) penalty += 10;
                count = 1;
            }
        }
        if (count >= 3) penalty += 10;

        return penalty;
    }

    private int calculateLevel(double entropy) {
        if (entropy < 28) return 1;
        if (entropy < 50) return 2;
        if (entropy < 65) return 3;
        if (entropy < 80) return 4;
        return 5;
    }
}