package Managers;

import Services.UserService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ServiceManager {

    private static boolean initialized = false;

    private static EntityManagerFactory emf;
    private static UserService userService;

    private ServiceManager() {}

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
