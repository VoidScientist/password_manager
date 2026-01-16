package Services;

import Entities.UserProfile;
import Managers.SessionManager;
import Repositories.UserProfileRepository;
import Managers.Interface.SessionListener;
import Utilities.Security.PasswordHasher;
import jakarta.persistence.*;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Classe s'occupant d'effectuer les actions telles que connecter, enregistrer et supprimer des utilisateurs.
 * Elle permet aussi de mettre à jour les profils utilisateur maître.
 *
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.2
 *
 * @see Repositories.UserProfileRepository
 * @see Entities.UserProfile
 *
 */
public class UserService implements SessionListener {

    private final String ALLOWED_REGEX = "^[a-zA-Z0-9_]*$";
    private final int MIN_USERNAME_LENGTH = 3;
    private final int MIN_PASSWORD_LENGTH = 8;

    private final EntityManager em;

    private final UserProfileRepository userRep;


    public UserService(EntityManager em) {

        this.em = em;

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


        boolean isPasswordValid;

        try {
            isPasswordValid = checkPassword(password, attemptTarget.getPasswordHash());
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("Erreur de hashage du mot de passe");
        }

        if (!isPasswordValid)
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
     * @throws IllegalArgumentException en cas de nom d'utilisateur dupliqué ou invalide
     * @throws Exception au cas où PasswordHasher renvoie une exception
     * @throws IllegalStateException si un utilisateur est déjà connecté
     */
    public void register(String username, char[] password)
        throws IllegalArgumentException, IllegalStateException, Exception {

        if (SessionManager.getCurrentUser() != null) {
            throw new IllegalStateException("Utilisateur déjà connecté");
        }

        EntityTransaction tx = em.getTransaction();

        if (!isUsernameValid(username)) {
            throw new IllegalArgumentException(
                    "Le nom d'utilisateur ne peut contenir que des chiffres et des lettres, ou un underscore (_). "
                    + "Il doit aussi faire plus de " + MIN_USERNAME_LENGTH + " caractères."
            );
        }

        if (password.length < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Mot de passe trop court: minimum " + MIN_PASSWORD_LENGTH + " caractères.");
        }

        try {

            tx.begin();

            String hash = PasswordHasher.hashPassword(password);

            UserProfile newProfile = new UserProfile(username, hash);

            userRep.save(newProfile);

            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            if (e.getClass().equals(IllegalStateException.class) || e.getClass().equals(NoSuchAlgorithmException.class)) {
                throw new Exception("Error hashing password");
            }

            if (e.getClass().equals(RollbackException.class)) {
                throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà!");
            }

        }

    }


    public void removeAccount(char[] password)
            throws IllegalStateException, IllegalArgumentException, Exception {

        if (SessionManager.getCurrentUser() == null) {
            throw new IllegalStateException("Pas d'utilisateur connecté");
        }

        UserProfile account = SessionManager.getCurrentUser();


        boolean isPasswordValid;
        try {
            isPasswordValid = checkPassword(password, account.getPasswordHash());
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("Erreur du hashage du mot de passe");
        }

        if (!isPasswordValid) {
            throw new IllegalArgumentException("Mauvais mot de passe");
        }

        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            userRep.delete(account);

            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            throw new Exception("Erreur lors de la déconnexion");

        }

    }
    
    public UserProfile updateProfile(UserProfile profile, String username, char[] password) throws Exception {

        EntityTransaction tx = em.getTransaction();        
        
        
        if (password.length < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Mot de passe trop court: minimum " + MIN_PASSWORD_LENGTH + " caractères.");
        }


        try {
            tx.begin();

            profile.setUsername(username);
            profile.setPasswordHash(PasswordHasher.hashPassword(password));

            UserProfile result = userRep.save(profile);

            tx.commit();

            return result;

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            throw new Exception("Mise à jour du profil échouée");

        }

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


    private static boolean checkPassword(char[] password, String hashedPassword)
            throws NoSuchAlgorithmException {

        String loginHash;

        String correctHash = hashedPassword.split("\\$")[1];
        String b64Salt = hashedPassword.split("\\$")[0];

        try {
            loginHash = PasswordHasher.hashPasswordFromSalt(b64Salt, password);
        } catch (Exception e) {
            throw new NoSuchAlgorithmException("Error hashing password");
        }

        return loginHash.equals(correctHash);

    }


    @Override
    public void onLogin() {}

    @Override
    public void onDisconnect() {
        this.em.clear();
    }
}