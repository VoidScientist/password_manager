package Repositories;


import Entities.UserProfile;
import Repositories.Interface.IRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class UserProfileRepository extends JPARepository<UserProfile> implements IRepository<UserProfile, String> {

    public UserProfileRepository(EntityManager entityManager) {
        super(entityManager);
    }

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

    @Override
    public UserProfile findById(String uuid) {

        return this.getEntityManager().find(UserProfile.class, uuid);

    }

    @Override
    public List<UserProfile> readAll() {

        return this.getEntityManager()
                .createQuery("select u from UserProfile u", UserProfile.class)
                .getResultList();

    }

}
