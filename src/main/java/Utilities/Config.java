package Utilities;

/**
 * Classe statique qui stocke et expose les constantes du projet.
 *
 */
public class Config {

    private static final String PERSISTENCE_UNIT = "secur2i";
    private static final String TEST_PERSISTENCE_UNIT = "test_db";

    public static String getPersistenceUnit() {
        return PERSISTENCE_UNIT;
    }

    public static String getTestPersistenceUnit() {
        return TEST_PERSISTENCE_UNIT;
    }

}
