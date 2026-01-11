package UI.panels;

import UI.MainFrame;

import javax.swing.*;
import java.awt.*;

public class SideMenu extends JPanel {

    private MainFrame mainFrame;
    private static final Color MENU_BG = new Color(88, 70, 150);
    private static final Color MENU_SELECTED = new Color(108, 90, 170);
    private static final Color MENU_HOVER = new Color(98, 80, 160);

    private JButton selectedButton = null;

    public SideMenu(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setPreferredSize(new Dimension(235, 600));
        setBackground(MENU_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Titre du menu
        JLabel titleLabel = new JLabel("Sécur2i");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 40, 0));
        add(titleLabel);

        // Séparateur
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.WHITE);
        separator.setMaximumSize(new Dimension(200, 1));
        add(separator);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // Boutons du menu
        addMenuButton("Mots de passe", "vault");
        addMenuButton("Sécurité", "security");
        addMenuButton("Score de sécurité", "security");
        addMenuButton("Générateur", "generator");
        addMenuButton("Gestion des comptes", "accounts");
        addMenuButton("Profil", "profile");

        // Espace flexible en bas
        add(Box.createVerticalGlue());
    }

    private void addMenuButton(String text, String pageName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setPreferredSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(MENU_BG);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effets hover et clic
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != selectedButton) {
                    button.setBackground(MENU_HOVER);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != selectedButton) {
                    button.setBackground(MENU_BG);
                }
            }
        });

        button.addActionListener(e -> {
            // Réinitialiser l'ancien bouton sélectionné
            if (selectedButton != null) {
                selectedButton.setBackground(MENU_BG);
            }

            // Marquer le nouveau bouton comme sélectionné
            button.setBackground(MENU_SELECTED);
            selectedButton = button;

            // Changer de page
            mainFrame.showPage(pageName);
        });

        add(button);
        add(Box.createRigidArea(new Dimension(0, 5)));
    }
}
