package UI.panels;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.security.SecureRandom;
import Utilities.Security.Password.*;

/**
 * Classe permettant d'afficher l'onglet de génération de mot de passe.
 * Elle utilise les classes utilitaires de Utilities.Security.Password
 */
public class PasswordGeneratorPanel extends JPanel {

    private JTextField passwordField;
    private JLabel lengthLabel;
    private JLabel minuscLabel;
    private JLabel majuscLabel;
    private JLabel chiffresLabel;
    private JLabel symbolesLabel;
    private JLabel[] strengthBars;

    private JCheckBox ambiguousCheckBox;
    private JCheckBox uppercaseCheckBox;
    private JCheckBox digitsCheckBox;
    private JCheckBox symbolsCheckBox;

    private JSpinner minSpinner;
    private JSpinner maxSpinner;

    private JButton generateButton;

    private static final SecureRandom random = new SecureRandom();

    // Couleurs pour les niveaux de sécurité
    private static final Color COLOR_LEVEL_1 = new Color(220, 53, 69);   // Rouge
    private static final Color COLOR_LEVEL_2 = new Color(253, 126, 20);  // Orange
    private static final Color COLOR_LEVEL_3 = new Color(255, 193, 7);   // Jaune
    private static final Color COLOR_LEVEL_4 = new Color(40, 167, 69);   // Vert
    private static final Color COLOR_LEVEL_5 = new Color(25, 135, 84);   // Vert foncé

    public PasswordGeneratorPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Titre
        JLabel titleLabel = new JLabel("Générateur de mots de passe");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // Champ de mot de passe généré
        passwordField = new JTextField();
        passwordField.setFont(new Font("Monospaced", Font.BOLD, 20));
        passwordField.setEditable(false);
        passwordField.setMaximumSize(new Dimension(550, 50));
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        add(passwordField);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Barre de force
        JPanel strengthPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        strengthPanel.setBackground(Color.WHITE);
        strengthBars = new JLabel[5];
        for (int i = 0; i < 5; i++) {
            strengthBars[i] = new JLabel("    ");
            strengthBars[i].setOpaque(true);
            strengthBars[i].setBackground(Color.LIGHT_GRAY);
            strengthBars[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            strengthPanel.add(strengthBars[i]);
        }
        strengthPanel.setMaximumSize(new Dimension(550, 30));
        add(strengthPanel);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Informations sur le mot de passe
        JPanel infoPanel = new JPanel(new GridLayout(2, 3, 10, 5));
        infoPanel.setMaximumSize(new Dimension(550, 60));
        infoPanel.setBackground(Color.WHITE);

        lengthLabel = new JLabel("Longueur: 0", SwingConstants.CENTER);
        minuscLabel = new JLabel("Minuscules: 0", SwingConstants.CENTER);
        chiffresLabel = new JLabel("Chiffres: 0", SwingConstants.CENTER);
        majuscLabel = new JLabel("Majuscules: 0", SwingConstants.CENTER);
        symbolesLabel = new JLabel("Symboles: 0", SwingConstants.CENTER);

        infoPanel.add(lengthLabel);
        infoPanel.add(new JLabel(""));
        infoPanel.add(new JLabel(""));
        infoPanel.add(minuscLabel);
        infoPanel.add(chiffresLabel);
        infoPanel.add(new JLabel(""));
        infoPanel.add(majuscLabel);
        infoPanel.add(symbolesLabel);

        add(infoPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // Titre Options
        JLabel optionsLabel = new JLabel("Options");
        optionsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        optionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(optionsLabel);
        add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel des options
        JPanel optionsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        optionsPanel.setMaximumSize(new Dimension(550, 150));
        optionsPanel.setBackground(Color.WHITE);

        // Colonne gauche - Contenu
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createTitledBorder("Contenu"));
        contentPanel.setBackground(Color.WHITE);

        ambiguousCheckBox = new JCheckBox("Caractères ambigus");
        ambiguousCheckBox.setSelected(true);
        ambiguousCheckBox.setBackground(Color.WHITE);
        uppercaseCheckBox = new JCheckBox("Majuscules");
        uppercaseCheckBox.setSelected(true);
        uppercaseCheckBox.setBackground(Color.WHITE);
        digitsCheckBox = new JCheckBox("Chiffres");
        digitsCheckBox.setSelected(true);
        digitsCheckBox.setBackground(Color.WHITE);
        symbolsCheckBox = new JCheckBox("Symboles");
        symbolsCheckBox.setSelected(true);
        symbolsCheckBox.setBackground(Color.WHITE);

        contentPanel.add(ambiguousCheckBox);
        contentPanel.add(uppercaseCheckBox);
        contentPanel.add(digitsCheckBox);
        contentPanel.add(symbolsCheckBox);

        // Colonne droite - Longueur
        JPanel lengthPanel = new JPanel();
        lengthPanel.setLayout(new BoxLayout(lengthPanel, BoxLayout.Y_AXIS));
        lengthPanel.setBorder(BorderFactory.createTitledBorder("Longueur"));
        lengthPanel.setBackground(Color.WHITE);

        JPanel minPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        minPanel.setBackground(Color.WHITE);
        minPanel.add(new JLabel("Minimum"));
        minSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 50, 1));
        minSpinner.setPreferredSize(new Dimension(60, 25));
        minPanel.add(minSpinner);

        JPanel maxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        maxPanel.setBackground(Color.WHITE);
        maxPanel.add(new JLabel("Maximum"));
        maxSpinner = new JSpinner(new SpinnerNumberModel(15, 1, 50, 1));
        maxSpinner.setPreferredSize(new Dimension(60, 25));
        maxPanel.add(maxSpinner);

        minSpinner.addChangeListener(e -> {
            int min = (Integer) minSpinner.getValue();
            int max = (Integer) maxSpinner.getValue();

            // Si min devient supérieur à max, ajuster max
            if (min > max) {
                maxSpinner.setValue(min);
            }
        });

        maxSpinner.addChangeListener(e -> {
            int min = (Integer) minSpinner.getValue();
            int max = (Integer) maxSpinner.getValue();

            // Si max devient inférieur à min, ajuster min
            if (max < min) {
                minSpinner.setValue(max);
            }
        });

        lengthPanel.add(minPanel);
        lengthPanel.add(maxPanel);

        optionsPanel.add(contentPanel);
        optionsPanel.add(lengthPanel);

        add(optionsPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // Bouton Générer
        generateButton = new JButton("Générer");
        generateButton.setFont(new Font("Arial", Font.BOLD, 16));
        generateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateButton.setPreferredSize(new Dimension(200, 40));
        generateButton.addActionListener(e -> generatePassword());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(generateButton);
        add(buttonPanel);

        // Générer un mot de passe initial
        generatePassword();
    }

    /**
     * Méthode de génération de mot de passe.
     * Utilise les classes utilitaires mentionnées dans la doc de classe.
     */
    private void generatePassword() {
        int min = (Integer) minSpinner.getValue();
        int max = (Integer) maxSpinner.getValue();

        // Ajuster automatiquement si min > max (sécurité supplémentaire)
        if (min > max) {
            max = min;
            maxSpinner.setValue(max);
        }

        // Longueur aléatoire entre min et max
        int length = min + random.nextInt(max - min + 1);

        try {
            String password = PasswordGenerator.generate(
                    length,
                    uppercaseCheckBox.isSelected(),
                    digitsCheckBox.isSelected(),
                    symbolsCheckBox.isSelected(),
                    ambiguousCheckBox.isSelected()
            );

            passwordField.setText(password);

            PasswordStats stats = new PasswordStats(password);
            updateStatsDisplay(stats);

            PasswordStrength strength = new PasswordStrength(password);
            updateStrengthDisplay(strength);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatsDisplay(PasswordStats stats) {
        lengthLabel.setText("Longueur: " + stats.getLength());
        minuscLabel.setText("Minuscules: " + stats.getLowercase());
        majuscLabel.setText("Majuscules: " + stats.getUppercase());
        chiffresLabel.setText("Chiffres: " + stats.getDigits());
        symbolesLabel.setText("Symboles: " + stats.getSymbols());
    }

    private void updateStrengthDisplay(PasswordStrength strength) {
        int level = strength.getLevel();

        // Déterminer la couleur selon le niveau
        Color levelColor;
        switch (level) {
            case 1: levelColor = COLOR_LEVEL_1; break;
            case 2: levelColor = COLOR_LEVEL_2; break;
            case 3: levelColor = COLOR_LEVEL_3; break;
            case 4: levelColor = COLOR_LEVEL_4; break;
            case 5: levelColor = COLOR_LEVEL_5; break;
            default: levelColor = Color.LIGHT_GRAY;
        }

        // Mettre à jour les barres de force
        for (int i = 0; i < 5; i++) {
            if (i < level) {
                strengthBars[i].setBackground(levelColor);
            } else {
                strengthBars[i].setBackground(Color.LIGHT_GRAY);
            }
        }
    }
}