package Services;

import Entities.UserProfile;
import Services.Interface.SessionListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Classe statique gérant les sessions utilisateurs avec un système de listener implémentant
 * l'interface {@code SessionListener}
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 *
 * @see Services.Interface.SessionListener
 * @see Entities.UserProfile
 *
 */
public class SessionManager {

    private static UserProfile currentUser;

    private static final Set<SessionListener> listeners = new HashSet<>();

    public static void setCurrentUser(UserProfile u) throws IllegalStateException {
        if (currentUser != null) {
            throw new IllegalStateException("UserProfile already set");
        }
        currentUser = u;
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

    public static void disconnect() {

        currentUser = null;
        listeners.forEach(SessionListener::onDisconnect);

    }

}
