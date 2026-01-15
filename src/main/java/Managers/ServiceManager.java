package Managers;

import Services.UserService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


/**
 * Classe statique de type {@code Manager} permettant d'accéder à tous les services n'importe où dans le code.
 * Utilise l'architecture Singleton.
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 *
 */
public class ServiceManager {

    private static boolean initialized = false;

    private static EntityManagerFactory emf;
    private static UserService userService;

    private ServiceManager() {}

    /**
     * Initialise les services de l'application et les expose via ServiceManager.
     *
     * @param persistenceUnitName unité de persistence à utiliser dans les services.
     */
    public static void init(String persistenceUnitName) {

        emf =  Persistence.createEntityManagerFactory(persistenceUnitName);
        userService = new UserService(emf);

        initialized = true;

    }

    public static UserService getUserService() {
        if (!initialized) {
            throw new IllegalStateException("ServiceManager has not been initialized");
        }

        return userService;
    }


}
