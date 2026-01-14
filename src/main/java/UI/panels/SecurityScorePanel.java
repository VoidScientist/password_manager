package UI.panels;

import Utilities.Security.Password.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SecurityScorePanel extends JPanel {

    private static final Color PURPLE_BG = new Color(88, 70, 150);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);
    private static final Color SCORE_EXCELLENT = new Color(76, 209, 55);  // Vert
    private static final Color SCORE_GOOD = new Color(156, 204, 101);     // Vert clair
    private static final Color SCORE_MEDIUM = new Color(255, 193, 7);     // Jaune/Orange
    private static final Color SCORE_POOR = new Color(255, 152, 0);       // Orange
    private static final Color SCORE_BAD = new Color(244, 67, 54);        // Rouge

    private int globalScore = 0;
    private List<SecurityWarning> warnings = new ArrayList<>();

    // Simulation de données (à remplacer par vraies données BDD)
    private List<PasswordEntry> passwordEntries = new ArrayList<>();

    public SecurityScorePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Charger le dictionnaire
        WeakPasswordDictionary.load();

        // Charger les données (simulation)
        loadPasswordData();

        // Calculer le score et les warnings
        calculateSecurityScore();

        // Panel principal avec padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Titre
        JLabel titleLabel = new JLabel("Score de sécurité");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel central avec le cercle de score et les warnings
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        // Cercle de score
        JPanel scoreCirclePanel = createScoreCircle();
        scoreCirclePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(scoreCirclePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Liste des warnings
        if (!warnings.isEmpty()) {
            JPanel warningsPanel = createWarningsPanel();
            contentPanel.add(warningsPanel);
        } else {
            JLabel noWarningsLabel = new JLabel("Bravo ! Votre sécurité est excellente.");
            noWarningsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            noWarningsLabel.setForeground(SCORE_EXCELLENT);
            noWarningsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(noWarningsLabel);
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void loadPasswordData() {
        // Simulation de données - À remplacer par les vraies données de la BDD
        passwordEntries.add(new PasswordEntry("Instagram", "azazafa586"));
        passwordEntries.add(new PasswordEntry("Facebook", "motdepasse123")); // Doublon !
        passwordEntries.add(new PasswordEntry("Gmail", "mom20"));
        passwordEntries.add(new PasswordEntry("Twitter", "#6K!_ucrojwm%vSGE0=A"));
        passwordEntries.add(new PasswordEntry("LinkedIn", "P@ssw0rd!2024"));
        passwordEntries.add(new PasswordEntry("Amazon", "uq70}^pSdjvb*2LTJ*:M"));
    }

    private void calculateSecurityScore() {
        warnings.clear();
        int totalScore = 100;
        int passwordCount = passwordEntries.size();

        if (passwordCount == 0) {
            globalScore = 0;
            return;
        }

        // 1. Analyser la robustesse moyenne
        double totalStrength = 0;
        List<String> weakPasswords = new ArrayList<>();
        List<String> mediumPasswords = new ArrayList<>();

        for (PasswordEntry entry : passwordEntries) {
            PasswordStrength strength = new PasswordStrength(entry.password);
            totalStrength += strength.getLevel();

            if (strength.getLevel() <= 2) {
                weakPasswords.add(entry.serviceName);
            } else if (strength.getLevel() == 3) {
                mediumPasswords.add(entry.serviceName);
            }
        }

        double averageStrength = totalStrength / passwordCount;

        // Pénalité pour robustesse faible
        if (averageStrength < 2.5) {
            totalScore -= 30;
        } else if (averageStrength < 3.5) {
            totalScore -= 15;
        }

        // 2. Détecter les mots de passe identiques
        Map<String, List<String>> passwordMap = new HashMap<>();
        for (PasswordEntry entry : passwordEntries) {
            passwordMap.computeIfAbsent(entry.password, k -> new ArrayList<>()).add(entry.serviceName);
        }

        for (Map.Entry<String, List<String>> entry : passwordMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                totalScore -= 15 * (entry.getValue().size() - 1);
                warnings.add(new SecurityWarning(
                        SecurityLevel.CRITICAL,
                        entry.getValue().size() + " mots de passe identiques !",
                        String.join(", ", entry.getValue())
                ));
            }
        }

        // 3. Ajouter les warnings pour mots de passe faibles
        if (!weakPasswords.isEmpty()) {
            totalScore -= 10 * weakPasswords.size();
            warnings.add(new SecurityWarning(
                    SecurityLevel.CRITICAL,
                    weakPasswords.size() + " mot(s) de passe très faible(s) détecté(s)",
                    String.join(", ", weakPasswords)
            ));
        }

        if (!mediumPasswords.isEmpty()) {
            warnings.add(new SecurityWarning(
                    SecurityLevel.WARNING,
                    mediumPasswords.size() + " mot(s) de passe de robustesse moyenne",
                    String.join(", ", mediumPasswords)
            ));
        }

        // 4. Vérifier les mots de passe dans le dictionnaire
        List<String> dictionaryPasswords = new ArrayList<>();
        for (PasswordEntry entry : passwordEntries) {
            if (WeakPasswordDictionary.isWeak(entry.password)) {
                dictionaryPasswords.add(entry.serviceName);
            }
        }

        if (!dictionaryPasswords.isEmpty()) {
            totalScore -= 20;
            warnings.add(new SecurityWarning(
                    SecurityLevel.CRITICAL,
                    "Mot(s) de passe courant(s) détecté(s)",
                    String.join(", ", dictionaryPasswords)
            ));
        }

        // 5. Détecter les patterns similaires
        List<String> similarPatterns = detectSimilarPatterns();
        if (!similarPatterns.isEmpty()) {
            warnings.add(new SecurityWarning(
                    SecurityLevel.WARNING,
                    "Mots de passe suivant des schémas similaires détectés",
                    String.join(", ", similarPatterns)
            ));
        }

        // 6. Longueur minimale
        List<String> shortPasswords = new ArrayList<>();
        for (PasswordEntry entry : passwordEntries) {
            if (entry.password.length() < 8) {
                shortPasswords.add(entry.serviceName);
            }
        }

        if (!shortPasswords.isEmpty()) {
            totalScore -= 10;
            warnings.add(new SecurityWarning(
                    SecurityLevel.WARNING,
                    "Mot(s) de passe trop court(s) (moins de 8 caractères)",
                    String.join(", ", shortPasswords)
            ));
        }

        globalScore = Math.max(0, Math.min(100, totalScore));

        // Trier les warnings : CRITICAL en premier, puis WARNING
        warnings.sort((w1, w2) -> {
            if (w1.level == w2.level) return 0;
            return w1.level == SecurityLevel.CRITICAL ? -1 : 1;
        });
    }

    private List<String> detectSimilarPatterns() {
        // Grouper les mots de passe par pattern
        Map<String, List<String>> patternMap = new HashMap<>();

        for (PasswordEntry entry : passwordEntries) {
            String pattern = extractPattern(entry.password);
            patternMap.computeIfAbsent(pattern, k -> new ArrayList<>()).add(entry.serviceName);
        }

        // Chercher les patterns utilisés plus de 2 fois
        List<String> similarAccounts = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : patternMap.entrySet()) {
            if (entry.getValue().size() >= 3) {
                similarAccounts.addAll(entry.getValue());
            }
        }

        return similarAccounts;
    }

    private String extractPattern(String password) {
        StringBuilder pattern = new StringBuilder();

        if (password.isEmpty()) return "";

        char prevType = getCharType(password.charAt(0));
        int count = 1;

        for (int i = 1; i < password.length(); i++) {
            char currentType = getCharType(password.charAt(i));
            if (currentType == prevType) {
                count++;
            } else {
                pattern.append(prevType);
                prevType = currentType;
                count = 1;
            }
        }
        pattern.append(prevType);

        return pattern.toString();
    }

    private char getCharType(char c) {
        if (Character.isDigit(c)) return 'D';
        if (Character.isLowerCase(c)) return 'L';
        if (Character.isUpperCase(c)) return 'U';
        return 'S'; // Symbole
    }

    private JPanel createScoreCircle() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 220;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Cercle coloré selon le score
                g2d.setColor(getScoreColor(globalScore));
                g2d.fillOval(x, y, size, size);

                // Score
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 80));
                String scoreText = String.valueOf(globalScore);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (size - fm.stringWidth(scoreText)) / 2;
                int textY = y + (size - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(scoreText, textX, textY);

                // Label "Score sécurité"
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                String label = "Score sécurité";
                fm = g2d.getFontMetrics();
                textX = x + (size - fm.stringWidth(label)) / 2;
                textY = textY + 30;
                g2d.drawString(label, textX, textY);

                g2d.dispose();
            }
        };

        panel.setPreferredSize(new Dimension(400, 250));
        panel.setMaximumSize(new Dimension(400, 250));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private Color getScoreColor(int score) {
        if (score >= 90) return SCORE_EXCELLENT;
        if (score >= 75) return SCORE_GOOD;
        if (score >= 60) return SCORE_MEDIUM;
        if (score >= 40) return SCORE_POOR;
        return SCORE_BAD;
    }

    private JPanel createWarningsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));

        for (SecurityWarning warning : warnings) {
            JPanel warningCard = createWarningCard(warning);
            panel.add(warningCard);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return panel;
    }

    private JPanel createWarningCard(SecurityWarning warning) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Icône de warning
        JLabel iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(40, 40));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(warning.level == SecurityLevel.CRITICAL ? SCORE_BAD : new Color(255, 152, 0));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        iconLabel.setText("!");
        iconLabel.setForeground(Color.WHITE);
        card.add(iconLabel, BorderLayout.WEST);

        // Message
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Color.WHITE);

        JLabel messageLabel = new JLabel(warning.message);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel affectedLabel = new JLabel(warning.affectedAccounts);
        affectedLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        affectedLabel.setForeground(Color.GRAY);

        messagePanel.add(messageLabel);
        messagePanel.add(Box.createRigidArea(new Dimension(0, 3)));
        messagePanel.add(affectedLabel);
        card.add(messagePanel, BorderLayout.CENTER);

        return card;
    }

    // Classes internes
    private static class PasswordEntry {
        String serviceName;
        String password;

        public PasswordEntry(String serviceName, String password) {
            this.serviceName = serviceName;
            this.password = password;
        }
    }

    private static class SecurityWarning {
        SecurityLevel level;
        String message;
        String affectedAccounts;

        public SecurityWarning(SecurityLevel level, String message, String affectedAccounts) {
            this.level = level;
            this.message = message;
            this.affectedAccounts = affectedAccounts;
        }
    }

    private enum SecurityLevel {
        CRITICAL,
        WARNING
    }
}