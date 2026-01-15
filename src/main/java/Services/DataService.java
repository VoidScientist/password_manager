package Services;


import Entities.Category;
import Entities.Profile;
import Managers.SessionManager;
import Repositories.CategoryRepository;
import Repositories.Interface.ISecureRepository;
import Repositories.ProfileRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

/**
 * Classe s'occupant d'effectuer les actions sur les Profile et Category.
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
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

    public DataService(EntityManagerFactory emf) {

        this.em = emf.createEntityManager();

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




    public void attachProfileToCategory(Profile prof, Category cat) {

        if (prof.getCategory() != null) {
            this.detachProfileFromCategory(prof);
        }

        cat.addProfile(prof);

    }

    public void detachProfileFromCategory(Profile prof) {
        Category profCat;

        if ((profCat = prof.getCategory()) != null) {

            profCat.removeProfile(prof);

        }

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

            throw new IllegalStateException("Impossible de sauvegarder " + errMsg + ".");
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

            throw new IllegalStateException("Impossible de supprimer " + errMsg + ".");

        }

    }

}
