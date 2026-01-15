package UI;

import Managers.Interface.SessionListener;
import Managers.SessionManager;
import UI.panels.*;

import javax.swing.*;
import java.awt.*;

// La fenêtre principale
public class MainFrame extends JFrame implements SessionListener {

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
        contentPanel.add(new RegisterPanel(this), "register");
        contentPanel.add(new PasswordGeneratorPanel(), "generator");
        contentPanel.add(new VaultPanel(), "vault");
        contentPanel.add(new SecurityScorePanel(), "securityscore");
        contentPanel.add(new CategoryManagementPanel(), "category");
        contentPanel.add(new AccountManagementPanel(), "profile");

        mainContainer.add(contentPanel, BorderLayout.CENTER);
        add(mainContainer);

        SessionManager.addListener(this);

        // Afficher la page de login par défaut
        showPage("login");
    }


    @Override
    public void onLogin() {
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
    @Override
    public void onDisconnect() {
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


}