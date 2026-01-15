package Managers.Interface;

/**
 * Une interface utilisée pour les listeners de déconnexion via {@code SessionManager}.
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 *
 * @see Managers.SessionManager
 *
 */
public interface SessionListener {

    void onDisconnect();

}
