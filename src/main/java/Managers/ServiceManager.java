package Managers;

import Services.DataService;
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
    private static DataService dataService;

    private ServiceManager() {}

    /**
     * Initialise les services de l'application et les expose via ServiceManager.
     *
     * @param persistenceUnitName unité de persistence à utiliser dans les services.
     */
    public static void init(String persistenceUnitName) {
        if (initialized) {
            throw new IllegalStateException("ServiceManager has already been initialized");
        }

        emf =  Persistence.createEntityManagerFactory(persistenceUnitName);
        userService = new UserService(emf);
        dataService = new DataService(emf);

        initialized = true;

    }

    public static UserService getUserService() {
        if (!initialized) {
            throw new IllegalStateException("ServiceManager has not been initialized");
        }

        return userService;
    }

    public static DataService getDataService() {
        if (!initialized) {
            throw new IllegalStateException("ServiceManager has not been initialized");
        }

        return dataService;
    }


}
