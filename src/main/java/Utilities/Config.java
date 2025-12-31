package Utilities;

/**
 * Classe statique qui stocke et expose les constantes du projet.
 *
 */
public class Config {

    private static final String PERSISTENCE_UNIT = "secur2i";

    public static String getPersistenceUnit() {
        return PERSISTENCE_UNIT;
    }

}
