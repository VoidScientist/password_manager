package Repositories;

import Entities.Profile;
import Repositories.Interface.ISecureRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

/**
 * Classe s'occupant d'effectuer des requêtes sur la base de donnée relatives aux entités {@code Profile}.
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 *
 * @see Entities.Profile
 *
 */
public class ProfileRepository extends JPARepository<Profile> implements ISecureRepository<Profile, Integer> {

    /**
     * Constructeur hérité de JPARepository.
     *
     * @param entityManager le gestionnaire d'entité auquel on fera les requêtes
     * @see JPARepository
     */
    public ProfileRepository(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Méthode permettant de récupérer un profil de mdp appartenant à un utilisateur donné.
     *
     * @param id id du profil dans la bdd
     * @param uuid uiid de l'utilisateur auquel elle appartient
     * @return le profil correspondant à l'id si elle existe, ou {@code null}
     */
    @Override
    public Profile findById(Integer id, String uuid) {
        try {
            return this.getEntityManager()
                    .createQuery("SELECT p FROM Profile p WHERE p.owner.uuid = :uuid AND p.id = :id", Profile.class)
                    .setParameter("uuid", uuid)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Méthode permettant de récupérer tous les profils de mdp appartenant à un utilisateur donné.
     *
     * @param uuid uiid de l'utilisateur dont on cherche les profils
     * @return une liste de profils
     */
    @Override
    public List<Profile> readAll(String uuid) {
        return this.getEntityManager()
                .createQuery("SELECT p FROM Profile p WHERE p.owner.uuid = :uuid", Profile.class)
                .setParameter("uuid", uuid)
                .getResultList();
    }

    public List<Profile> findByCategoryName(String catName, String uuid) {
        return this.getEntityManager()
                .createQuery("SELECT p FROM Profile p WHERE p.owner.uuid = :uuid AND p.category.name = :name", Profile.class)
                .setParameter("uuid", uuid)
                .setParameter("name", catName)
                .getResultList();

    }

}
