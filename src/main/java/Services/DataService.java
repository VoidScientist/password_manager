package Services;


import Entities.Category;
import Entities.Profile;
import Managers.SessionManager;
import Repositories.CategoryRepository;
import Repositories.Interface.ISecureRepository;
import Repositories.ProfileRepository;
import Utilities.Security.Encryption.PasswordEncrypter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe s'occupant d'effectuer les actions sur les Profile et Category.
 * Permet aussi de réencrypter les mots de passe lors de la MaJ du profil maître.
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.2
 *
 * @see Repositories.ProfileRepository
 * @see Repositories.CategoryRepository
 * @see Entities.Profile
 * @see Entities.Category
 *
 */
public class DataService {

    private final EntityManager em;

    private final CategoryRepository catRep;
    private final ProfileRepository profRep;

    public DataService(EntityManager em) {

        this.em = em;

        catRep = new CategoryRepository(em);
        profRep = new ProfileRepository(em);

    }

    public Category createCategory(String name, String description) throws IllegalStateException {

        if (SessionManager.getCurrentUser() == null) {
            throw new IllegalStateException("Aucun utilisateur n'est connecté");
        }

        Category newCategory = new Category(name, description);

        SessionManager.getCurrentUser().addCategory(newCategory);

        return this.saveCategory(newCategory);

    }

    public void updateCategory(Category category, String name, String description) throws IllegalStateException {

        category.setName(name.strip());
        category.setDesc(description.strip());

        this.saveCategory(category);

    }

    public Profile createProfile(String service, String username, String email, String password, String url)
            throws IllegalStateException {

        if (SessionManager.getCurrentUser() == null) {
            throw new IllegalStateException("Aucun utilisateur n'est connecté");
        }

        Profile newProfile = new Profile(service, username, password, url);
        newProfile.setEmail(email);

        SessionManager.getCurrentUser().addProfile(newProfile);

        return this.saveProfile(newProfile);

    }

    public void removeCategory(Category cat) throws IllegalStateException {
        try {
            SessionManager.getCurrentUser().removeCategory(cat);
        } catch (IllegalArgumentException ignored) {}

        removeData(cat, this.catRep, "la catégorie");
    }

    public void removeProfile(Profile profile) throws IllegalStateException {
        try {
            SessionManager.getCurrentUser().removeProfile(profile);
        } catch (IllegalArgumentException ignored) {}

        removeData(profile, this.profRep, "le profil");
    }


    /**
     * FIX: Re-encryption transactionnelle avec rollback automatique en cas d'erreur
     *
     * @param oldHash Ancien hash du mot de passe maître
     * @param newHash Nouveau hash du mot de passe maître
     * @throws IllegalStateException Si la re-encryption échoue (avec rollback automatique)
     */
    public void reencryptPasswords(String oldHash, String newHash) throws IllegalStateException {

        EntityTransaction tx = this.em.getTransaction();

        // Sauvegarder les anciens mots de passe encryptés pour rollback manuel si besoin
        List<Profile> profiles = getProfiles();
        List<String> oldEncryptedPasswords = new ArrayList<>();

        for (Profile profile : profiles) {
            oldEncryptedPasswords.add(profile.getEncrypted_password());
        }

        try {
            tx.begin();

            for (int i = 0; i < profiles.size(); i++) {
                Profile profile = profiles.get(i);

                try {
                    // Décrypter avec l'ancien hash
                    String plainPassword = PasswordEncrypter.decrypt(profile.getEncrypted_password(), oldHash);

                    // Re-encrypter avec le nouveau hash
                    String newEncryptedPassword = PasswordEncrypter.encrypt(plainPassword, newHash);

                    // Mettre à jour
                    profile.setEncryptedPassword(newEncryptedPassword);
                    profRep.save(profile);

                } catch (Exception e) {
                    System.err.println("ERREUR lors de la re-encryption du profil " + profile.getService() + ": " + e.getMessage());
                    throw new IllegalStateException("Impossible de re-encrypter le profil " + profile.getService(), e);
                }
            }

            tx.commit();
            System.out.println("Re-encryption réussie pour " + profiles.size() + " profils");

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
                System.err.println("ROLLBACK de la re-encryption effectué");
            }

            throw new IllegalStateException("Échec de la re-encryption. Tous les changements ont été annulés.", e);
        }
    }


    public List<Category> getCategories() {

        String uuid = SessionManager.getCurrentUser().getUuid();

        return this.catRep.readAll(uuid);

    }

    public List<Profile> getProfiles() {

        String uuid = SessionManager.getCurrentUser().getUuid();

        return this.profRep.readAll(uuid);

    }


    public void attachProfileToCategory(Profile prof, Category cat) {

        if (cat == null) return;

        this.detachProfileFromCategory(prof);

        cat.addProfile(prof);

    }


    public void detachProfileFromCategory(Profile prof) {
        Category profCat;

        if ((profCat = prof.getCategory()) != null) {
            try {
                profCat.removeProfile(prof);
            } catch (Exception ignored) {}

        }

    }

    public List<Profile> findProfilesByCategoryName(String catName) {

        return this.profRep.findByCategoryName(catName, SessionManager.getCurrentUser().getUuid());

    }

    public Category findCategoryByName(String name) {
        return this.catRep.findByName(name, SessionManager.getCurrentUser().getUuid());
    }


    public Category saveCategory(Category cat) throws IllegalStateException {
        return this.saveData(cat, this.catRep, "la catégorie");
    }

    public Profile saveProfile(Profile prof) throws IllegalStateException {
        return saveData(prof, this.profRep, "le profil");
    }

    private <T> T saveData(T model, ISecureRepository<T, ?> rep, String errMsg) throws IllegalStateException {

        EntityTransaction tx = this.em.getTransaction();

        try {

            tx.begin();

            T managed = rep.save(model);

            tx.commit();

            return managed;

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            // FIX: Log l'erreur pour faciliter le débogage
            System.err.println("ERREUR lors de la sauvegarde de " + errMsg + ": " + e.getMessage());
            e.printStackTrace();

            throw new IllegalStateException("Impossible de sauvegarder " + errMsg + ".", e);
        }

    }

    private <T> void removeData(T model, ISecureRepository<T, ?> rep, String errMsg) throws IllegalStateException {

        EntityTransaction tx = this.em.getTransaction();

        try {

            tx.begin();

            rep.delete(model);

            tx.commit();


        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            // FIX: Log l'erreur pour faciliter le débogage
            System.err.println("ERREUR lors de la suppression de " + errMsg + ": " + e.getMessage());
            e.printStackTrace();

            throw new IllegalStateException("Impossible de supprimer " + errMsg + ".", e);

        }

    }

}