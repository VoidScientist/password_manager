package Managers;

import Entities.UserProfile;
import Managers.Interface.SessionListener;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Classe statique gérant les sessions utilisateurs avec un système de listener implémentant
 * l'interface {@code SessionListener}
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 *
 * @see SessionListener
 * @see Entities.UserProfile
 *
 */
public class SessionManager {

    private static UserProfile currentUser;

    private static final Set<SessionListener> listeners = new HashSet<>();

    /**
     * FIX: Permet maintenant de se reconnecter après une déconnexion
     */
    public static void setCurrentUser(UserProfile u) throws IllegalStateException {
        if (currentUser != null) {
            System.err.println("WARNING: Un utilisateur était déjà connecté. Déconnexion automatique.");
            disconnect(); // Déconnecter l'utilisateur précédent
        }
        currentUser = u;
        listeners.forEach(SessionListener::onLogin);
    }

    public static UserProfile getCurrentUser() {
        return currentUser;
    }

    public static void addListener(SessionListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(SessionListener listener) {
        listeners.remove(listener);
    }

    public static void updateCurrentProfile(UserProfile updated) {

        if (!Objects.equals(updated.getUuid(), currentUser.getUuid())) {
            throw new IllegalArgumentException("Les UUIDs doivent être identiques");
        }

        currentUser = updated;

    }

    public static boolean isConnected() {
        return currentUser != null;
    }

    /**
     * FIX: Notification des listeners avant de clear currentUser
     */
    public static void disconnect() {
        if (currentUser != null) {
            // Notifier les listeners AVANT de clear
            listeners.forEach(SessionListener::onDisconnect);
            currentUser = null;
        }
    }

}