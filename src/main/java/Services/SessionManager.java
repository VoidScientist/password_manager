package Services;

import Entities.UserProfile;
import Services.Interface.SessionListener;

import java.util.HashSet;
import java.util.Set;

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
