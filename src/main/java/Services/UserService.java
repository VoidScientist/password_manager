package Services;

import Entities.UserProfile;
import Repositories.UserProfileRepository;
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
public class UserService {

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
     */
    public UserProfile login(String username, char[] password)
            throws IllegalArgumentException, Exception {

        UserProfile attemptTarget = userRep.findByUsername(username);

        if (attemptTarget == null) {
            throw new IllegalArgumentException("Username not found");
        }

        String loginHash;
        String b64Salt = attemptTarget.getPasswordHash().split("\\$")[0];

        try {
            loginHash = PasswordHasher.hashPasswordFromSalt(b64Salt, password);
        } catch (Exception e) {
            throw new Exception("Error hashing password");
        }

        if (!loginHash.equals(attemptTarget.getPasswordHash()))
            throw new IllegalArgumentException("Wrong password");

        return attemptTarget;

    }

    /**
     *
     * Méthode utilisée pour enregistrer les utilisateurs.
     *
     * @param username nom de l'utilisateur à enregistrer (doit être unique)
     * @param password mot de passe de l'utilisateur à enregistrer
     * @return profil de l'utilisateur, géré par l'entity manager de UserService
     * @throws IllegalArgumentException en cas de nom d'utilisateur dupliqué
     * @throws Exception au cas où PasswordHasher renvoie une exception
     */
    public UserProfile register(String username, char[] password)
        throws IllegalArgumentException, Exception {

        EntityTransaction tx = em.getTransaction();
        UserProfile attemptTarget;

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
                throw new IllegalArgumentException("Username already exists");
            }

            return null;

        }

        return attemptTarget;

    }


}