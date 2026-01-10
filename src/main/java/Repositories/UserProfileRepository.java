package Repositories;


import Entities.UserProfile;
import Repositories.Interface.IRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;


/**
 * Classe s'occupant d'effectuer des requêtes sur la base de donnée relatives aux entités {@code UserProfile}.
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 *
 * @see Entities.UserProfile
 *
 */
public class UserProfileRepository extends JPARepository<UserProfile> implements IRepository<UserProfile, String> {

    /**
     * Constructeur hérité de JPARepository.
     *
     * @param entityManager le gestionnaire d'entité auquel on fera les requêtes
     * @see JPARepository
     */
    public UserProfileRepository(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Méthode permettant d'obtenir le profil d'un utilisateur via son nom.
     *
     * @param username Nom de l'utilisateur dans la base de donnée (unique)
     * @return Profil utilisateur s'il existe ou {@code null}
     */
    public UserProfile findByUsername(String username) {

        try {
            return this.getEntityManager()
                    .createQuery("select u from UserProfile u where u.username = :username", UserProfile.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    /**
     * Méthode permettant de retrouver un profil utilisateur via son uuid
     * <br>
     * Wrapper de {@code EntityManager.find()}
     *
     * @param uuid Identifiant unique de l'utilisateur dans la bdd
     * @return Profil utilisateur s'il existe ou {@code null}
     */
    @Override
    public UserProfile findById(String uuid) {

        return this.getEntityManager().find(UserProfile.class, uuid);

    }

    /**
     * Méthode permettant de récupérer une liste de tous les utilisateurs enregistrés.
     *
     * @return liste des profils utilisateur enregistrés dans la bdd
     */
    @Override
    public List<UserProfile> readAll() {

        return this.getEntityManager()
                .createQuery("select u from UserProfile u", UserProfile.class)
                .getResultList();

    }

}
