package Repositories;

import Entities.Profile;
import Repositories.Interface.ISecureRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class ProfileRepository extends JPARepository<Profile> implements ISecureRepository<Profile, Integer> {

    public ProfileRepository(EntityManager entityManager) {
        super(entityManager);
    }

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

    @Override
    public List<Profile> readAll(String uuid) {
        return this.getEntityManager()
                .createQuery("SELECT p FROM Profile p WHERE p.owner.uuid = :uuid", Profile.class)
                .setParameter("uuid", uuid)
                .getResultList();
    }


}
