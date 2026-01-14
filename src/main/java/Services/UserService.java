package Services;

import Entities.UserProfile;
import Repositories.UserProfileRepository;
import Services.Interface.SessionListener;
import Utilities.Security.PasswordHasher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.RollbackException;

import java.security.NoSuchAlgorithmException;

/**
 * Classe s'occupant d'effectuer les actions telles que connecter, enregistrer et supprimer des utilisateurs.
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 *
 * @see Repositories.UserProfileRepository
 * @see Entities.UserProfile
 *
 */
public class UserService implements SessionListener {

    private final String ALLOWED_REGEX = "^[a-zA-Z0-9_]*$";
    private final int MIN_USERNAME_LENGTH = 3;

    private final EntityManagerFactory emf;
    private final EntityManager em;

    private final UserProfileRepository userRep;


    public UserService(EntityManagerFactory emf) {

        this.emf = emf;
        this.em = emf.createEntityManager();

        this.userRep = new UserProfileRepository(em);

    }

    /**
     *
     * Méthode utilisée pour connecter les utilisateurs
     *
     * @param username nom de l'utilisateur à connecter
     * @param password mot de passe de l'utilisateur à connecter
     * @return le UserProfile géré par l'entity manager de UserService
     * @throws IllegalArgumentException en cas de mauvais nom d'utilisateur ou mot de passe
     * @throws Exception au cas où le PasswordHasher rencontre une erreur
     * @throws IllegalStateException si un utilisateur est déjà connecté
     */
    public UserProfile login(String username, char[] password)
            throws IllegalArgumentException, IllegalStateException, Exception {

        UserProfile attemptTarget = userRep.findByUsername(username);

        if (attemptTarget == null) {
            throw new IllegalArgumentException("Il n'existe pas d'utilisateurs avec ce nom d'utilisateur.");
        }

        String loginHash;

        String correctHash = attemptTarget.getPasswordHash().split("\\$")[1];
        String b64Salt = attemptTarget.getPasswordHash().split("\\$")[0];

        try {
            loginHash = PasswordHasher.hashPasswordFromSalt(b64Salt, password);
        } catch (Exception e) {
            throw new Exception("Error hashing password");
        }

        if (!loginHash.equals(correctHash))
            throw new IllegalArgumentException("Mauvais mot de passe");

        try {
            SessionManager.setCurrentUser(attemptTarget);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Impossible de se connecter: un utilisateur est déjà connecté.");
        }

        return attemptTarget;

    }

    /**
     *
     * Méthode utilisée pour enregistrer les utilisateurs.
     *
     * @param username nom de l'utilisateur à enregistrer (doit être unique et être alphanumérique + _)
     * @param password mot de passe de l'utilisateur à enregistrer
     * @return profil de l'utilisateur, géré par l'entity manager de UserService
     * @throws IllegalArgumentException en cas de nom d'utilisateur dupliqué ou invalide
     * @throws Exception au cas où PasswordHasher renvoie une exception
     * @throws IllegalStateException si un utilisateur est déjà connecté
     */
    public UserProfile register(String username, char[] password)
        throws IllegalArgumentException, IllegalStateException, Exception {

        EntityTransaction tx = em.getTransaction();
        UserProfile attemptTarget;

        if (!isUsernameValid(username)) {
            throw new IllegalArgumentException(
                    "Le nom d'utilisateur ne peut contenir que des chiffres et des lettres, ou un underscore (_). "
                    + "Il doit aussi faire plus de " + MIN_USERNAME_LENGTH + " caractères."
            );
        }

        try {

            tx.begin();

            String hash = PasswordHasher.hashPassword(password);

            UserProfile newProfile = new UserProfile(username, hash);

            attemptTarget = userRep.save(newProfile);

            tx.commit();

        } catch (Exception e) {

            if (e.getClass().equals(IllegalStateException.class) || e.getClass().equals(NoSuchAlgorithmException.class)) {
                throw new Exception("Error hashing password");
            }

            if (e.getClass().equals(RollbackException.class)) {
                throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà!");
            }

            return null;

        }

        try {
            SessionManager.setCurrentUser(attemptTarget);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Impossible de se connecter: un utilisateur est déjà connecté.");
        }

        return attemptTarget;

    }

    /**
     *
     * Méthode vérifiant la validité d'un nom d'utilisateur avec du regex en fonction de {@code ALLOWED_REGEX}
     * Ne permet pas les noms d'utilisateur vide.
     *
     * @param username nom d'utilisateur à vérifier
     * @return si le nom d'utilisateur est valide ou non
     */
    private boolean isUsernameValid(String username) {

        boolean isValid = username.matches(ALLOWED_REGEX);
        boolean hasEnoughCharacters = username.length() >= MIN_USERNAME_LENGTH;

        return isValid && hasEnoughCharacters;

    }

    @Override
    public void onDisconnect() {
        this.em.clear();
    }
}