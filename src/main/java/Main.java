import Managers.ServiceManager;
import UI.MainFrame;
import Utilities.Config;

import javax.swing.*;

/**
 * Point d'entrée du programme. 
 * Crée l'UI et initialise le ServiceManager.
 */
public class Main {

    public static void main(String[] args) {

        ServiceManager.init(Config.getPersistenceUnit());

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
