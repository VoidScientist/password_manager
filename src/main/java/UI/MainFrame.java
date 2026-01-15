package UI;

import Managers.Interface.SessionListener;
import Managers.SessionManager;
import UI.panels.*;

import javax.swing.*;
import java.awt.*;

// La fenêtre principale de l'application

public class MainFrame extends JFrame implements SessionListener {

    private SideMenu sideMenu;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel mainContainer;

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;

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

        // Créer et stocker les références aux panels
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);

        // Ajouter les différentes pages
        contentPanel.add(loginPanel, "login");
        contentPanel.add(registerPanel, "register");
        contentPanel.add(new PasswordGeneratorPanel(), "generator");
        contentPanel.add(new VaultPanel(), "vault");
        contentPanel.add(new SecurityScorePanel(), "securityscore");
        contentPanel.add(new CategoryManagementPanel(), "category");
        contentPanel.add(new AccountManagementPanel(), "profile");

        mainContainer.add(contentPanel, BorderLayout.CENTER);
        add(mainContainer);

        // Enregistrer ce MainFrame comme listener du SessionManager
        SessionManager.addListener(this);

        // Afficher la page de login par défaut
        showPage("login");
    }

    // Méthode appelée lors de la connexion réussie
    @Override
    public void onLogin() {
        isLoggedIn = true;

        // Nettoyer les champs du LoginPanel
        loginPanel.clearFields();

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
    @Override
    public void onDisconnect() {
        isLoggedIn = false;

        // Retirer et cacher le menu latéral
        sideMenu.setVisible(false);
        mainContainer.remove(sideMenu);

        // Nettoyer les champs du LoginPanel avant de l'afficher
        loginPanel.clearFields();

        // Retourner à la page de login
        showPage("login");

        // Rafraîchir l'affichage
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    // Méthode pour changer de page
    public void showPage(String pageName) {
        // Nettoyer les champs avant d'afficher une page
        if (pageName.equals("login")) {
            registerPanel.clearFields();
        } else if (pageName.equals("register")) {
            loginPanel.clearFields();
        }

        cardLayout.show(contentPanel, pageName);
    }
}