package Repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TransactionRequiredException;
import jakarta.transaction.Transactional;

/**
 * Classe parent des {@code XXXXRepository}, contient un constructeur et des méthodes génériques pour {@code save()}
 * et {@code delete()}
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 *
 * @see UserProfileRepository
 * @see CategoryRepository
 * @see ProfileRepository
 *
 */
public class JPARepository<ModelType> {

    private final EntityManager em;

    /**
     * Constructeur permettant de récupérer un {@code EntityManager}
     *
     * @param entityManager le gestionnaire d'entité sur lequel on effectuera des requêtes
     */
    public JPARepository(EntityManager entityManager) {

        this.em = entityManager;

    }

    public EntityManager getEntityManager() {

        return this.em;

    }


    /**
     * Méthode permettant de sauvegarder une entité dans la bdd.
     * <br> <br>
     * ⚠ Doit être appelé dans une transaction ou une erreur sera renvoyée.
     *
     * @param model Entité à {@code persist()} ou {@code merge()} en fonction de son existence
     * @throws TransactionRequiredException si l'appel n'a pas lieu dans une transaction gérée par le
     *                                      gestionnaire d'entité utilisé par la classe.
     *
     * @return l'objet géré par le gestionnaire d'entité
     */
    public ModelType save(ModelType model) throws TransactionRequiredException {

        if (!em.getTransaction().isActive()) {
            throw new TransactionRequiredException();
        }

        if (em.contains(model)) {
            model = em.merge(model);
        } else {
            em.persist(model);
        }

        return model;

    }


    /**
     * Méthode permettant de supprimer de la bdd une entité gérée par le gestionnaire d'entité
     * <br> <br>
     * ⚠ Doit être appelé dans une transaction ou une erreur sera renvoyée.
     *
     * @param model Entité à {@code remove()}
     * @throws TransactionRequiredException si l'appel n'a pas lieu dans une transaction gérée par le
     *                                      gestionnaire d'entité utilisé par la classe.
     */
    public void delete(ModelType model) throws TransactionRequiredException {

        if (!em.getTransaction().isActive()) {
            throw new TransactionRequiredException();
        }

        if (em.contains(model)) {
            em.remove(model);
        }

    }

}
