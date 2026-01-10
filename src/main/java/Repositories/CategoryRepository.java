package Repositories;

import Entities.Category;
import Repositories.Interface.ISecureRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;


/**
 * Classe s'occupant d'effectuer des requêtes sur la base de donnée relatives aux entités {@code Category}.
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 *
 * @see Entities.Category
 *
 */
public class CategoryRepository extends JPARepository<Category> implements ISecureRepository<Category, Integer> {

    /**
     * Constructeur hérité de JPARepository.
     *
     * @param entityManager le gestionnaire d'entité auquel on fera les requêtes
     * @see JPARepository
     */
    public CategoryRepository(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Méthode permettant de récupérer une catégorie appartenant à un utilisateur donné.
     *
     * @param id id de la catégory dans la bdd
     * @param uuid uiid de l'utilisateur auquel elle appartient
     * @return la catégorie correspondant à l'id si elle existe, ou {@code null}
     */
    @Override
    public Category findById(Integer id, String uuid) {
        try {
            return this.getEntityManager()
                    .createQuery("SELECT c FROM Category c WHERE c.owner.uuid = :uuid AND c.id = :id", Category.class)
                    .setParameter("uuid", uuid)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Méthode permettant de récupérer toutes les catégories appartenant à un utilisateur donné.
     *
     * @param uuid uiid de l'utilisateur dont on cherche les catégories
     * @return une liste de catégories
     */
    @Override
    public List<Category> readAll(String uuid) {
        return this.getEntityManager()
                .createQuery("SELECT c FROM Category c WHERE c.owner.uuid = :uuid", Category.class)
                .setParameter("uuid", uuid)
                .getResultList();
    }

}
