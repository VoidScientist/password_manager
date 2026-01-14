package UI;

import UI.panels.*;

import javax.swing.*;
import java.awt.*;

// La fenêtre principale
public class MainFrame extends JFrame {

    private SideMenu sideMenu;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel mainContainer;

    private boolean isLoggedIn = false;

    public MainFrame() {
        setTitle("Sécur2i - Gestionnaire de Mots de Passe");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Container principal
        mainContainer = new JPanel(new BorderLayout());

        // Menu latéral (caché au début)
        sideMenu = new SideMenu(this);
        sideMenu.setVisible(false);

        // Panel central avec CardLayout pour les pages
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        // Ajouter les différentes pages
        contentPanel.add(new LoginPanel(this), "login");
        contentPanel.add(new SecurityPanel(), "security");
        contentPanel.add(new PasswordGeneratorPanel(), "generator");
        contentPanel.add(new VaultPanel(), "vault");
        contentPanel.add(new SecurityScorePanel(), "securityscore");
        contentPanel.add(new ProfilePanel(), "profile");

        mainContainer.add(contentPanel, BorderLayout.CENTER);
        add(mainContainer);

        // Afficher la page de login par défaut
        showPage("login");
    }

    // Méthode appelée après connexion réussie
    public void onLoginSuccess() {
        isLoggedIn = true;

        // Ajouter le menu à gauche
        mainContainer.add(sideMenu, BorderLayout.WEST);
        sideMenu.setVisible(true);

        // Aller à la première page avec menu
        showPage("vault");

        // Rafraîchir l'affichage
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    // Méthode appelée lors de la déconnexion
    public void onLogout() {
        isLoggedIn = false;

        // Retirer et cacher le menu latéral
        sideMenu.setVisible(false);
        mainContainer.remove(sideMenu);

        // Retourner à la page de login
        showPage("login");

        // Rafraîchir l'affichage
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    // Méthode pour changer de page
    public void showPage(String pageName) {
        cardLayout.show(contentPanel, pageName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

// ===================== Autres pages à implémenter =====================

class SecurityPanel extends JPanel {
    public SecurityPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel label = new JLabel("PAGE SÉCURITÉ", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 32));
        add(label, BorderLayout.CENTER);
    }
}

class ProfilePanel extends JPanel {
    public ProfilePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel label = new JLabel("PROFIL", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 32));
        add(label, BorderLayout.CENTER);
    }
}